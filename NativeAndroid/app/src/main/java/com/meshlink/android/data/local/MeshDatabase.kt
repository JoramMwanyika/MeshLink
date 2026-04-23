package com.meshlink.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.meshlink.android.data.local.dao.MessageDao
import com.meshlink.android.data.local.entity.DeviceEntity
import com.meshlink.android.data.local.entity.MessageEntity

@Database(entities = [MessageEntity::class, DeviceEntity::class], version = 1)
abstract class MeshDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
