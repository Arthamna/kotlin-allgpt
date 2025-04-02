package com.example.AllGpt.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.example.AllGpt.domain.model.TopicEntity
import com.example.AllGpt.data.database.AppDatabase
import com.example.AllGpt.domain.model.MessageEntity
import com.example.AllGpt.domain.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: AppDatabase,
    private val llmModel: LLMRepository
) : ChatRepository {
    private val userDao = database.userDao()
    private val topicDao = database.topicDao()
    private val messageDao = database.messageDao()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentTopic = MutableStateFlow<TopicEntity?>(null)
    override val currentTopic: StateFlow<TopicEntity?> = _currentTopic.asStateFlow()

    private val _topicList = MutableStateFlow<List<TopicEntity>>(emptyList())
    override val topicList: StateFlow<List<TopicEntity>> = _topicList.asStateFlow()

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    override val messages: StateFlow<List<MessageEntity>> = _messages.asStateFlow()

    init {
        repositoryScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                    Log.d("Topic","Topic is added")
                topicDao.getTopicsForUser(userId)
                    .collect { topicList ->
                        _topicList.value = topicList
                    }
            }
        }
    }

    override suspend fun clearUserData(userId: String) {
        withContext(Dispatchers.IO) {
            try {
                database.withTransaction {
                    val userTopics = topicDao.getTopicsForUser(userId).first()

                    userTopics.forEach { topic ->
                        topic.topicId?.let { topicId ->
                            messageDao.deleteMessagesForTopic(topicId)
                        }
                    }

                    topicDao.deleteTopicsForUser(userId)
                }

                repositoryScope.launch {
                    _messages.value = emptyList()
                    _topicList.value = emptyList()
                    _currentTopic.value = null
                }
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error clearing user data", e)
                throw e
            }
        }
    }

    override suspend fun createTopic(title: String): Result<TopicEntity> {
        return try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

            val topic = TopicEntity(
                topicId = 0,
                userId = userId,
                title = title,
                lastMessage = null
            )

            val newTopicId = topicDao.insertTopic(topic).toInt()
            val newTopic = topic.copy(topicId = newTopicId)
            setCurrentTopicAndMessage(newTopic)
            Result.success(newTopic)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setCurrentTopicAndMessage(topic: TopicEntity?) {
        _currentTopic.value = topic
        topic?.let {
            messageDao.getMessagesForTopic(it.topicId)
                .collect { messageList ->
                    _messages.value = messageList
                }
        }
    }

    override suspend fun sendMessage(topicId: Int, content: String): Result<MessageEntity> {
        return try {
            val topicCount = topicDao.getTopicById(topicId) ?: throw IllegalStateException("Topic not found")
            val message = MessageEntity(
                messageId = 0,
                topicId = topicCount.topicId,
                content = content,
                isFromUser = true,
                timestamp =  SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )

            messageDao.insertMessage(message)
            _messages.value += message

            topicDao.getTopicById(topicId)?.let { topic ->
                val updatedTopic = topic.copy(lastMessage = content)
                topicDao.updateTopic(updatedTopic)
                if (_currentTopic.value?.topicId == topicId) {
                    _currentTopic.value = updatedTopic
                }
            }

            val aiResponse = generateAIResponse(content)
            val aiMessage = MessageEntity(
                messageId = 0,
                topicId = topicId,
                content = aiResponse,
                isFromUser = false,
                timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )

            messageDao.insertMessage(aiMessage)
            _messages.value += aiMessage

            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generateAIResponse(userMessage: String): String {
        return try {
            llmModel.generateResponse(userMessage)
        } catch (e: Exception) {
            "Sorry, I couldn't process that message. Please try again."
        }
    }


}
