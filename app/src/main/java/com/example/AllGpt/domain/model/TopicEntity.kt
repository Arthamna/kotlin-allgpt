package com.example.AllGpt.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey(autoGenerate = true)
    val topicId: Int,
    val userId: String,   // Foreign key UserEntity
    val title: String,
    val lastMessage: String?,
    val lastSyncTimestamp:  String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
)
