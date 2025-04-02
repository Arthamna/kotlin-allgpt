package com.example.AllGpt.domain.repository

import com.example.AllGpt.util.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(userName: String, email: String, password: String): Flow<Resource<FirebaseUser?>>
    suspend fun loginUser(email: String, password: String): Flow<Resource<FirebaseUser?>>
}