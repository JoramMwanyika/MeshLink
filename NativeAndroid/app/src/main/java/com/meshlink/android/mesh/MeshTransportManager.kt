package com.meshlink.android.mesh

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import java.util.*

class MeshTransportManager(
    private val context: Context,
    private val identityManager: IdentityManager
) {
    private val MESH_BLE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")
    private val MESH_MESSAGE_CHAR_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")
    
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bluetoothBackend = BluetoothBackendManager(bluetoothAdapter)
    private val scannerManager = ScannerManager(context, bluetoothAdapter)
    private val accountManager = AccountManager(context)
    
    private var isScanning = false
    private var isAdvertising = false
    private var gattServer: BluetoothGattServer? = null

    // Callbacks
    var onPeerDiscovered: ((deviceId: String, name: String?, address: String, rssi: Int) -> Unit)? = null
    var onMessageReceived: ((payload: String) -> Unit)? = null

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray
        ) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
            if (characteristic.uuid == MESH_MESSAGE_CHAR_UUID) {
                val payload = String(value, Charsets.UTF_8)
                Log.d("MeshTransport", "Received GATT write from ${device.address}: $payload")
                onMessageReceived?.invoke(payload)
                if (responseNeeded) {
                    try {
                        gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                    } catch (e: SecurityException) {
                        Log.e("MeshTransport", "Security exception sending GATT response", e)
                    }
                }
            }
        }
    }

    init {
        bluetoothBackend.onMessageReceived = { payload ->
            onMessageReceived?.invoke(payload)
        }

        scannerManager.onDeviceDiscovered = { device ->
            try {
                val name = device.name ?: "Unknown Peer"
                val address = device.address
                onPeerDiscovered?.invoke(address, name, address, -50)
                
                // Automatically attempt to connect to the discovered peer to establish a data pipe
                connectToPeer(address)
            } catch (e: SecurityException) {
                Log.e("MeshTransport", "Permission missing for device name", e)
            }
        }
    }

    fun startMeshServices(deviceId: String) {
        setupGattServer()
        bluetoothBackend.startListening()
        startAdvertising(deviceId)
        startScanning()
    }

    private fun setupGattServer() {
        if (bluetoothAdapter == null) {
            Log.w("MeshTransport", "Bluetooth adapter is null, skipping GATT server setup.")
            return
        }
        try {
            gattServer = bluetoothManager.openGattServer(context, gattServerCallback)
            if (gattServer == null) {
                Log.w("MeshTransport", "GATT server is null (Bluetooth might be disabled or unsupported).")
                return
            }
            val service = BluetoothGattService(MESH_BLE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
            val characteristic = BluetoothGattCharacteristic(
                MESH_MESSAGE_CHAR_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE
            )
            service.addCharacteristic(characteristic)
            gattServer?.addService(service)
        } catch (e: SecurityException) {
            Log.e("MeshTransport", "Missing permissions for GATT Server", e)
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            isAdvertising = true
            Log.d("MeshTransport", "BLE Advertising started")
        }
        override fun onStartFailure(errorCode: Int) {
            isAdvertising = false
            Log.e("MeshTransport", "BLE Advertising failed: $errorCode")
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val deviceId = result.scanRecord?.getServiceData(ParcelUuid(MESH_BLE_UUID))?.let { 
                String(it, Charsets.UTF_8) 
            }
            if (deviceId != null) {
                onPeerDiscovered?.invoke(deviceId, result.device.name, result.device.address, result.rssi)
                // In a real mesh, we might keep track of peers to connect later
                lastDiscoveredDevice = result.device
            }
        }
    }

    private var lastDiscoveredDevice: BluetoothDevice? = null

    @SuppressLint("MissingPermission")
    fun startAdvertising(deviceId: String) {
        // Always try to set the friendly name so classic discovery sees it
        if (identityManager.isSetupComplete()) {
            identityManager.getUsername()?.let { name ->
                bluetoothAdapter?.name = name
            }
        }

        val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser ?: run {
            Log.w("MeshTransport", "BLE Advertiser not available on this device")
            return
        }

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(true)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceUuid(ParcelUuid(MESH_BLE_UUID))
            .addServiceData(ParcelUuid(MESH_BLE_UUID), deviceId.toByteArray(Charsets.UTF_8))
            .build()

        try {
            advertiser.startAdvertising(settings, data, advertiseCallback)
        } catch (e: SecurityException) {
            Log.e("MeshTransport", "Missing BLE permissions", e)
        }
    }

    fun startScanning() {
        if (isScanning) return
        
        // Try to start BLE Scan if available
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        if (scanner != null) {
            val filter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(MESH_BLE_UUID))
                .build()
                
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            try {
                scanner.startScan(listOf(filter), settings, scanCallback)
                Log.d("MeshTransport", "BLE Scanning started")
            } catch (e: SecurityException) {
                Log.e("MeshTransport", "Missing BLE permissions", e)
            }
        } else {
            Log.w("MeshTransport", "BLE Scanner not available on this device")
        }

        // Always start Classic Bluetooth Scan
        scannerManager.startScanning()
        isScanning = true
    }

    fun stopScanning() {
        // Stop BLE Scan if available
        bluetoothAdapter?.bluetoothLeScanner?.let { scanner ->
            try {
                scanner.stopScan(scanCallback)
            } catch (e: SecurityException) {
                Log.e("MeshTransport", "Missing BLE permissions", e)
            }
        }
        
        // Always stop Classic Bluetooth Scan
        scannerManager.stopScanning()
        isScanning = false
    }

    fun broadcastMessage(payload: String) {
        Log.i("MeshTransport", "Initiating Mesh Broadcast (RFCOMM)")
        bluetoothBackend.broadcastMessage(payload)
        
        // Fallback or secondary broadcast via BLE GATT if needed
        lastDiscoveredDevice?.let { device ->
            connectAndWrite(device, payload)
        }
    }

    fun connectToPeer(address: String) {
        bluetoothAdapter?.getRemoteDevice(address)?.let { device ->
            bluetoothBackend.connectToDevice(device)
        }
    }

    private fun connectAndWrite(device: BluetoothDevice, payload: String) {
        try {
            device.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d("MeshTransport", "Connected to ${device.address}, discovering services...")
                        gatt.discoverServices()
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        gatt.close()
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        val service = gatt.getService(MESH_BLE_UUID)
                        val characteristic = service?.getCharacteristic(MESH_MESSAGE_CHAR_UUID)
                        if (characteristic != null) {
                            characteristic.value = payload.toByteArray(Charsets.UTF_8)
                            gatt.writeCharacteristic(characteristic)
                            Log.d("MeshTransport", "Payload written to ${device.address}")
                        }
                    }
                }

                override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                    Log.d("MeshTransport", "Write finished for ${device.address}, disconnecting.")
                    gatt.disconnect()
                }
            })
        } catch (e: SecurityException) {
            Log.e("MeshTransport", "Security exception during GATT connect", e)
        }
    }
}
