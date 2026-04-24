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
    fun simulateIncomingPeer(id: String, name: String) {
        // Disabled
    }

    fun simulateIncomingMessage(senderId: String, content: String) {
        // Disabled
    }

    fun startAutoSimulation() {
        // Disabled
    }
}
