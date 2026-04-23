package com.meshlink.android

import android.app.Application
import androidx.room.Room
import com.meshlink.android.data.local.MeshDatabase
import com.meshlink.android.mesh.IdentityManager
import com.meshlink.android.mesh.MeshTransportManager
import com.meshlink.android.mesh.MessageRelayEngine
import com.meshlink.android.mesh.MeshSimulator

class MeshLinkApplication : Application() {
    
    lateinit var database: MeshDatabase
    lateinit var transportManager: MeshTransportManager
    lateinit var identityManager: IdentityManager
    lateinit var relayEngine: MessageRelayEngine
    lateinit var simulator: MeshSimulator

    override fun onCreate() {
        super.onCreate()
        
        database = Room.databaseBuilder(
            applicationContext,
            MeshDatabase::class.java,
            "meshlink_db"
        ).build()

        identityManager = IdentityManager(this)
        transportManager = MeshTransportManager(this)
        
        relayEngine = MessageRelayEngine(
            database.messageDao(),
            transportManager,
            identityManager
        )

        simulator = MeshSimulator(transportManager, identityManager)
        
        // Start mesh services
        transportManager.startScanning()
        transportManager.startAdvertising(identityManager.getDeviceId())
        
        // Optional: Start auto-simulation for UI testing
         simulator.startAutoSimulation()
    }
}
