package com.example.AllGpt.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.AllGpt.domain.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE topicId = :topicId ORDER BY timestamp ASC")
    fun getMessagesForTopic(topicId: Int): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE topicId IN (:topicIds) ORDER BY timestamp ASC")
    fun getMessagesForAllTopics(topicIds: List<Int>): Flow<List<MessageEntity>>

    @Query("DELETE FROM messages WHERE topicId = :topicId")
    suspend fun deleteMessagesForTopic(topicId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

}
