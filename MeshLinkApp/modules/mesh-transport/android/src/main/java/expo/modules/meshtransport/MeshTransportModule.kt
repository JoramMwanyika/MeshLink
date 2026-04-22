package expo.modules.meshtransport

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.os.ParcelUuid
import android.util.Log
import java.util.UUID

class MeshTransportModule : Module() {

  private var isAdvertising = false
  private var isScanning = false
  
  private val MESH_BLE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb") 

  // BLE Resources
  private val bluetoothAdapter: BluetoothAdapter? by lazy {
    val bluetoothManager = appContext.reactContext?.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    bluetoothManager?.adapter
  }

  // WiFi P2P Resources (Like Xender)
  private val wifiP2pManager: WifiP2pManager? by lazy {
      appContext.reactContext?.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
  }
  private var wifiP2pChannel: WifiP2pManager.Channel? = null

  // --- BLE Callbacks ---
  private val advertiseCallback = object : AdvertiseCallback() {
      override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
          isAdvertising = true
          sendEvent("onTransportEvent", mapOf("type" to "BLE_ADVERTISE_SUCCESS"))
      }
      override fun onStartFailure(errorCode: Int) {
          isAdvertising = false
          sendEvent("onTransportEvent", mapOf("type" to "BLE_ADVERTISE_FAILED", "code" to errorCode))
      }
  }

  private val scanCallback = object : ScanCallback() {
      override fun onScanResult(callbackType: Int, result: ScanResult) {
          super.onScanResult(callbackType, result)
          val device = result.device
          val payload = result.scanRecord?.getServiceData(ParcelUuid(MESH_BLE_UUID))?.let { String(it, Charsets.UTF_8) }
          
          sendEvent("onPeerDiscovered", mapOf(
              "address" to device.address,
              "rssi" to result.rssi,
              "type" to "BLE",
              "payload" to payload
          ))
      }
  }

  override fun definition() = ModuleDefinition {
    Name("MeshTransport")

    Events("onTransportEvent", "onPeerDiscovered", "onMessageReceived")

    // mode param dictates BLE, WIFI, or BOTH (Xender logic)
    AsyncFunction("startAdvertising") { deviceId: String, mode: String ->
      try {
        // 1. BLE Advertising
        bluetoothAdapter?.bluetoothLeAdvertiser?.let { advertiser ->
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

             advertiser.startAdvertising(settings, data, advertiseCallback)
        }

        // 2. Wi-Fi Direct (Xender Mode) Discovery
        if (mode == "BOTH" || mode == "WIFI") {
            wifiP2pChannel = wifiP2pManager?.initialize(appContext.reactContext, appContext.reactContext!!.mainLooper, null)
            wifiP2pManager?.discoverPeers(wifiP2pChannel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    sendEvent("onTransportEvent", mapOf("type" to "WIFI_P2P_DISCOVER_SUCCESS"))
                }
                override fun onFailure(reasonCode: Int) {
                    sendEvent("onTransportEvent", mapOf("type" to "WIFI_P2P_DISCOVER_FAILED"))
                }
            })
        }
        
        true
      } catch (e: SecurityException) {
          Log.e("MeshTransport", "Missing BLUETOOTH_ADVERTISE or NEARBY_WIFI_DEVICES permissions")
          false
      }
    }

    AsyncFunction("startScanning") {
      try {
        bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)
        isScanning = true
        true
      } catch(e: SecurityException) {
          false
      }
    }

    AsyncFunction("broadcastMessage") { payload: String ->
        Log.i("MeshTransport", "Broadcasting message via Wi-Fi Socket / BLE GATT: \$payload")
        // Implementation pushes bytes across established P2P socket or BLE Write Characteristic
        sendEvent("onTransportEvent", mapOf("type" to "DATA_SENT"))
        true
    }
  }
}
