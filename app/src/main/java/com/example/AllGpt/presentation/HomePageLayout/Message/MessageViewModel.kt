package com.example.AllGpt.presentation.HomePageLayout.Message

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AllGpt.data.database.MessageDao
import com.example.AllGpt.domain.model.MessageEntity
import com.example.AllGpt.domain.model.TopicEntity
import com.example.AllGpt.domain.use_case.ChatUseCase
import com.example.AllGpt.presentation.HomePageLayout.Topic.TopicViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase,

) : ViewModel() {
    val currentTopic = chatUseCase.currentTopic
    val messages = chatUseCase.messages

    fun sendMessage(content: String) {
        viewModelScope.launch {
            currentTopic.value?.let { topic ->
                chatUseCase.sendMessage(topic.topicId, content)
            }
        }
    }
}

