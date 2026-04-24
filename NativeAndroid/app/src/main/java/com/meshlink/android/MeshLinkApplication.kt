package com.meshlink.android

import android.app.Application
import androidx.room.Room
import com.meshlink.android.data.local.MeshDatabase
import com.meshlink.android.data.repository.DeviceRepository
import com.meshlink.android.data.repository.MessageRepository
import com.meshlink.android.mesh.IdentityManager
import com.meshlink.android.mesh.MeshTransportManager
import com.meshlink.android.mesh.MessageRelayEngine
import com.meshlink.android.mesh.MeshSimulator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeshLinkApplication : Application() {
    
    lateinit var database: MeshDatabase
    lateinit var transportManager: MeshTransportManager
    lateinit var identityManager: IdentityManager
    lateinit var relayEngine: MessageRelayEngine
    lateinit var simulator: MeshSimulator

    lateinit var deviceRepository: DeviceRepository
    lateinit var messageRepository: MessageRepository

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        
        // --- FRESH START BLOCK ---
        // Uncomment these lines if you want to wipe everything on EVERY launch.
        // For this run, I am wiping the preferences once to reset your identity.
        getSharedPreferences("meshlink_identity", MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("MeshLinkPrefs", MODE_PRIVATE).edit().clear().apply()
        // -------------------------

        database = Room.databaseBuilder(
            applicationContext,
            MeshDatabase::class.java,
            "meshlink_db"
        ).fallbackToDestructiveMigration().build()

        deviceRepository = DeviceRepository(database.deviceDao())
        messageRepository = MessageRepository(database.messageDao())

        identityManager = IdentityManager(this)
        transportManager = MeshTransportManager(this, identityManager)
        
        transportManager.onPeerDiscovered = { deviceId, name, _, rssi ->
            applicationScope.launch {
                deviceRepository.saveDiscoveredDevice(deviceId, name, rssi)
            }
        }

        relayEngine = MessageRelayEngine(
            database.messageDao(),
            deviceRepository,
            transportManager,
            identityManager
        )

        simulator = MeshSimulator(transportManager, identityManager)
        
        // Mesh services should be started after permissions are granted in MainActivity
    }
}
