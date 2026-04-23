package com.meshlink.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val receiverId: String?,
    val groupId: String?,
    val content: String,
    val timestamp: Long,
    val ttl: Int,
    val status: String // "pending", "sent", "relayed", "delivered"
)
