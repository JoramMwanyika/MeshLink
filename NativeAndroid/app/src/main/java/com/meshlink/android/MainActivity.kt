package com.meshlink.android

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.meshlink.android.ui.theme.MeshLinkTheme
import com.meshlink.android.ui.screens.MainNavigation
import com.meshlink.android.ui.viewmodel.MeshViewModel
import com.meshlink.android.ui.viewmodel.MeshViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: MeshViewModel by viewModels {
        val app = application as MeshLinkApplication
        MeshViewModelFactory(app.deviceRepository, app.messageRepository, app.relayEngine, app.transportManager)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d("MeshLink", "Permissions result: $permissions")
        if (permissions.values.all { it }) {
            checkAndEnableBluetooth()
        } else {
            Log.w("MeshLink", "Not all permissions granted: ${permissions.filter { !it.value }.keys}")
        }
    }

    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MeshLink", "Bluetooth enable result: ${result.resultCode}")
        if (result.resultCode == RESULT_OK) {
            actuallyStartMeshServices()
        } else {
            Log.w("MeshLink", "Bluetooth not enabled by user")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkAndRequestPermissions()

        setContent {
            MeshLinkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.meshlink.android.ui.theme.MeshBlack
                ) {
                    MainNavigation(viewModel)
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()
        
        // Always include Location as it's required for discovery results
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            Log.d("MeshLink", "All permissions already granted")
            checkAndEnableBluetooth()
        } else {
            Log.d("MeshLink", "Requesting permissions: $missingPermissions")
            requestPermissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    private fun checkAndEnableBluetooth() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        
        if (bluetoothAdapter == null) {
            Log.e("MeshLink", "Bluetooth Adapter is NULL - Hardware not supported?")
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Log.d("MeshLink", "Bluetooth is OFF, requesting enable...")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            Log.d("MeshLink", "Bluetooth is already ON")
            actuallyStartMeshServices()
        }
    }

    private fun actuallyStartMeshServices() {
        try {
            Log.d("MeshLink", "Actually starting mesh services...")
            val app = application as MeshLinkApplication
            app.transportManager.startMeshServices(app.identityManager.getDeviceId())
            
            // Make the device discoverable so other emulators can find it
            requestDiscoverable()
        } catch (e: Exception) {
            Log.e("MeshLink", "CRASH in actuallyStartMeshServices", e)
        }
    }

    private fun requestDiscoverable() {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300) // 5 minutes
        }
        startActivity(discoverableIntent)
    }
}
