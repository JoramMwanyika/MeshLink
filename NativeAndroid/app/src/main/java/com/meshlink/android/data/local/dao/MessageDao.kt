package com.meshlink.android.data.local.dao

import androidx.room.*
import com.meshlink.android.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: MessageEntity)

    @Update
    suspend fun update(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE senderId = :peerId OR receiverId = :peerId ORDER BY timestamp ASC")
    fun getMessagesForChat(peerId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages GROUP BY CASE WHEN senderId = 'SELF' THEN receiverId ELSE senderId END ORDER BY timestamp DESC")
    fun getRecentChats(): Flow<List<MessageEntity>>
}
