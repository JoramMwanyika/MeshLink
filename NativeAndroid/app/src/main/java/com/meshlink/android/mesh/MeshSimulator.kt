package com.meshlink.android.mesh

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

/**
 * Helper class to simulate mesh events for testing and demonstration.
 */
class MeshSimulator(
    private val transportManager: MeshTransportManager,
    private val identityManager: IdentityManager
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    fun simulateIncomingPeer(id: String, name: String) {
        Log.d("MeshSimulator", "Simulating peer discovery: $id ($name)")
        transportManager.onPeerDiscovered?.invoke(id, "00:11:22:33:44:55", -50)
    }

    fun simulateIncomingMessage(senderId: String, content: String) {
        val myId = identityManager.getDeviceId()
        val json = JSONObject().apply {
            put("id", "sim-msg-" + UUID.randomUUID().toString().substring(0, 5))
            put("sender_id", senderId)
            put("receiver_id", myId)
            put("content", content)
            put("timestamp", System.currentTimeMillis())
            put("ttl", 5)
        }
        
        Log.d("MeshSimulator", "Simulating incoming message from $senderId")
        transportManager.onMessageReceived?.invoke(json.toString())
    }

    fun startAutoSimulation() {
        scope.launch {
            delay(2000)
            simulateIncomingPeer("ML-SIM-SARAH", "Sarah")
            delay(3000)
            simulateIncomingMessage("ML-SIM-SARAH", "Hey! The mesh is working perfectly in Kotlin.")
        }
    }
}
