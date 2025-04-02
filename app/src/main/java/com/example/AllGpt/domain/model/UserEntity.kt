package com.example.AllGpt.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val username: String,
    val email: String,
    val password: String
)



