package com.example.AllGpt.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val messageId: Int,
    val topicId: Int,   // Foreign key TopicEntity
    val content: String,
    val isFromUser: Boolean,
    val timestamp: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
)
