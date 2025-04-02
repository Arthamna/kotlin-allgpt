package com.example.AllGpt.domain.use_case

import com.example.AllGpt.domain.repository.AuthRepository
import javax.inject.Inject

class AuthenticationUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend fun registerUser(userName: String, email: String, password: String) =
        repository.registerUser(userName, email, password)

    suspend fun loginUser(email: String, password: String) =
        repository.loginUser(email, password)
}