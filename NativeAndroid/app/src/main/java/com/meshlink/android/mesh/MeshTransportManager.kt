package com.meshlink.android.mesh

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import java.util.*

class MeshTransportManager(private val context: Context) {
    private val MESH_BLE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")
    private val MESH_MESSAGE_CHAR_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")
    
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    
    private var isScanning = false
    private var isAdvertising = false
    private var gattServer: BluetoothGattServer? = null

    // Callbacks
    var onPeerDiscovered: ((deviceId: String, address: String, rssi: Int) -> Unit)? = null
    var onMessageReceived: ((payload: String) -> Unit)? = null

    init {
        setupGattServer()
    }

    private fun setupGattServer() {
        try {
            gattServer = bluetoothManager.openGattServer(context, gattServerCallback)
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
                    gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                }
            }
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
                onPeerDiscovered?.invoke(deviceId, result.device.address, result.rssi)
                // In a real mesh, we might keep track of peers to connect later
                lastDiscoveredDevice = result.device
            }
        }
    }

    private var lastDiscoveredDevice: BluetoothDevice? = null

    fun startAdvertising(deviceId: String) {
        val advertiser = bluetoothAdapter?.bluetoothLeAdvertiser ?: return
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
        val scanner = bluetoothAdapter?.bluetoothLeScanner ?: return
        if (isScanning) return
        
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(MESH_BLE_UUID))
            .build()
            
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            scanner.startScan(listOf(filter), settings, scanCallback)
            isScanning = true
        } catch (e: SecurityException) {
            Log.e("MeshTransport", "Missing BLE permissions", e)
        }
    }

    fun broadcastMessage(payload: String) {
        Log.i("MeshTransport", "Initiating BLE Mesh Broadcast (GATT Write)")
        
        // Real logic: Connect to discovered peers and write
        // For demonstration, we attempt to connect to the 'lastDiscoveredDevice'
        lastDiscoveredDevice?.let { device ->
            connectAndWrite(device, payload)
        } ?: Log.w("MeshTransport", "No peers found to broadcast to.")
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
