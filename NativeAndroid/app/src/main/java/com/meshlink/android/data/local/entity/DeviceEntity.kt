package com.meshlink.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val deviceId: String,
    val name: String,
    val lastSeen: Long,
    val rssi: Int = 0,
    val color: Int = 0xFF00FF66.toInt()
)
