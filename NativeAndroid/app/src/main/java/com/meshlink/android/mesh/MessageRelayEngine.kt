package com.meshlink.android.mesh

import android.util.Log
import com.meshlink.android.data.local.dao.MessageDao
import com.meshlink.android.data.local.entity.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

class MessageRelayEngine(
    private val messageDao: MessageDao,
    private val deviceRepository: com.meshlink.android.data.repository.DeviceRepository,
    private val transportManager: MeshTransportManager,
    private val identityManager: IdentityManager
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        transportManager.onMessageReceived = { payload ->
            processIncomingPayload(payload)
        }
    }

    private fun processIncomingPayload(payload: String) {
        try {
            val json = JSONObject(payload)
            val id = json.getString("id")
            val senderId = json.getString("sender_id")
            val senderName = json.optString("sender_name", "Unknown Peer")
            val receiverId = json.optString("receiver_id", null)
            val groupId = json.optString("group_id", null)
            val content = json.getString("content")
            val timestamp = json.getLong("timestamp")
            val ttl = json.getInt("ttl")

            scope.launch {
                // 0. Update sender info in devices table
                deviceRepository.saveDiscoveredDevice(senderId, senderName, -50)

                // 1. Deduplication: Check if we've seen this message
                val existing = messageDao.getMessageById(id)
                if (existing != null) {
                    Log.d("RelayEngine", "Duplicate message received, ignoring: $id")
                    return@launch
                }

                val myId = identityManager.getDeviceId()
                val isForMe = receiverId == myId || (groupId != null) // Simplification: all group msgs stored

                // 2. Store locally
                val status = if (isForMe) "received" else "relayed"
                val entity = MessageEntity(
                    id = id,
                    senderId = senderId,
                    receiverId = receiverId,
                    groupId = groupId,
                    content = content,
                    timestamp = timestamp,
                    ttl = ttl - 1,
                    status = status
                )
                messageDao.insert(entity)

                // 3. Relay logic
                if (!isForMe && entity.ttl > 0) {
                    Log.i("RelayEngine", "Relaying message $id. Remaining TTL: ${entity.ttl}")
                    transportManager.broadcastMessage(payload)
                } else if (isForMe) {
                    Log.i("RelayEngine", "Message $id delivered to self.")
                }
            }
        } catch (e: Exception) {
            Log.e("RelayEngine", "Failed to parse incoming payload", e)
        }
    }

    fun sendMessage(receiverId: String?, groupId: String?, content: String) {
        val myId = identityManager.getDeviceId()
        val myName = identityManager.getUsername() ?: "User"
        val messageId = "msg-" + UUID.randomUUID().toString().substring(0, 8)
        val timestamp = System.currentTimeMillis()
        val ttl = 5

        val message = MessageEntity(
            id = messageId,
            senderId = "SELF", // Use "SELF" for local DB consistency
            receiverId = receiverId,
            groupId = groupId,
            content = content,
            timestamp = timestamp,
            ttl = ttl,
            status = "pending"
        )

        scope.launch {
            messageDao.insert(message)
            
            val json = JSONObject().apply {
                put("id", messageId)
                put("sender_id", myId)
                put("sender_name", myName)
                put("receiver_id", receiverId)
                put("group_id", groupId)
                put("content", content)
                put("timestamp", timestamp)
                put("ttl", ttl)
            }
            
            transportManager.broadcastMessage(json.toString())
            
            messageDao.update(message.copy(status = "sent"))
        }
    }
}
