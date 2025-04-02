package com.example.AllGpt.presentation.HomePageLayout.Topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AllGpt.domain.model.TopicEntity
import com.example.AllGpt.domain.use_case.ChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TopicViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase
) : ViewModel() {

    val currentTopic = chatUseCase.currentTopic
    val topicList = chatUseCase.topicList


    fun selectTopic(topic: TopicEntity) {
        viewModelScope.launch {
            chatUseCase.setCurrentTopicAndMessage(topic)
        }
    }

    fun createNewTopic(title: String) {
        viewModelScope.launch {
            chatUseCase.createTopic(title).onSuccess {
            }
        }
    }
}