package com.example.AllGpt.domain.repository

import com.example.AllGpt.domain.model.TopicEntity
import com.example.AllGpt.domain.model.MessageEntity
import kotlinx.coroutines.flow.StateFlow

interface ChatRepository {
    val currentTopic: StateFlow<TopicEntity?>
    val topicList: StateFlow<List<TopicEntity>>
    val messages: StateFlow<List<MessageEntity>>

    suspend fun setCurrentTopicAndMessage(topic: TopicEntity?)
    suspend fun createTopic(title: String): Result<TopicEntity>
    suspend fun sendMessage(topicId: Int, content: String): Result<MessageEntity>
    suspend fun clearUserData(userId: String)
}