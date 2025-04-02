package com.example.AllGpt.domain.use_case

import com.example.AllGpt.data.repository.ChatRepositoryImpl
import com.example.AllGpt.domain.model.TopicEntity
import com.example.AllGpt.domain.repository.ChatRepository
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val repository: ChatRepository,
//    private val tes : ChatRepositoryImpl
) {
    //flow state
    val currentTopic = repository.currentTopic
    val topicList = repository.topicList
    val messages = repository.messages

    suspend fun setCurrentTopicAndMessage(topic: TopicEntity?) =
        repository.setCurrentTopicAndMessage(topic)
    suspend fun sendMessage(topicId: Int, content: String) =
        repository.sendMessage(topicId, content)
    suspend fun createTopic(title: String) =
        repository.createTopic(title)
    suspend fun clearUserData(userId: String) =
        repository.clearUserData(userId)
//    suspend fun clearUserData(userId: String) =
//        tes.clearUserData(userId)
}