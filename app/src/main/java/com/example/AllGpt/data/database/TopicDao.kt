package com.example.AllGpt.data.database

import com.example.AllGpt.domain.model.TopicEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE userId = :userId ORDER BY lastSyncTimestamp DESC")
    fun getTopicsForUser(userId: String): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity): Long

    @Update
    suspend fun updateTopic(topic: TopicEntity)

    @Query("SELECT * FROM topics WHERE topicId = :topicId")
    suspend fun getTopicById(topicId: Int): TopicEntity?

    @Query("DELETE FROM topics WHERE userId = :userId")
    suspend fun deleteTopicsForUser(userId: String)
}
