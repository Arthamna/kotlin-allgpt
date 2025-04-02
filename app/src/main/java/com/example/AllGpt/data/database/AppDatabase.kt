package com.example.AllGpt.data.database

import com.example.AllGpt.domain.model.TopicEntity
import com.example.AllGpt.domain.model.UserEntity
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.AllGpt.domain.model.MessageEntity


@Database(
    entities = [UserEntity::class, TopicEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun topicDao(): TopicDao
    abstract fun messageDao(): MessageDao

}
