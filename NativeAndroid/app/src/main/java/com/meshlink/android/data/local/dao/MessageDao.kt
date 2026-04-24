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

    @Query("""
        SELECT * FROM messages 
        WHERE (senderId = :myUuid AND receiverId = :peerUuid) 
        OR (senderId = :peerUuid AND receiverId = :myUuid) 
        ORDER BY timestamp ASC
    """)
    suspend fun getChatWithPeer(myUuid: String, peerUuid: String): List<MessageEntity>

    @Query("""
        SELECT * FROM messages 
        GROUP BY CASE WHEN senderId = 'SELF' THEN receiverId ELSE senderId END 
        ORDER BY timestamp DESC
    """)
    suspend fun getRecentChatsOverview(): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE senderId = :peerId OR receiverId = :peerId ORDER BY timestamp ASC")
    suspend fun getChatHistory(peerId: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE status = 'pending'")
    suspend fun getPendingMessages(): List<MessageEntity>

    @Query("SELECT * FROM messages GROUP BY CASE WHEN senderId = 'SELF' THEN receiverId ELSE senderId END ORDER BY timestamp DESC")
    fun getRecentChats(): Flow<List<MessageEntity>>

    @Query("""
        SELECT m.*, d.name as peerName, 
        CASE WHEN m.senderId = 'SELF' THEN m.receiverId ELSE m.senderId END as peerId
        FROM messages m
        LEFT JOIN devices d ON d.deviceId = CASE WHEN m.senderId = 'SELF' THEN m.receiverId ELSE m.senderId END
        GROUP BY peerId
        ORDER BY m.timestamp DESC
    """)
    fun getRecentChatsWithNames(): Flow<List<com.meshlink.android.data.local.dto.RecentChat>>
}
