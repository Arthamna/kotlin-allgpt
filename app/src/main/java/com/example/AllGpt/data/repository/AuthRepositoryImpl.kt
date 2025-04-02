package com.example.AllGpt.data.repository

import com.example.AllGpt.data.database.UserDao
import com.example.AllGpt.domain.model.UserEntity
import com.example.AllGpt.domain.repository.AuthRepository
import com.example.AllGpt.util.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val userDao: UserDao // Inject UserDao
) : AuthRepository {

    override suspend fun registerUser(
        userName: String,
        email: String,
        password: String
    ): Flow<Resource<FirebaseUser?>> = flow {
        emit(Resource.Loading)
        try {
            val result = suspendCoroutine<AuthResult> { continuation ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        continuation.resume(result)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            result.user?.let { user ->
                val userId = user.uid
                // Create local user entity
                val userEntity = UserEntity(
                    userId = userId,
                    username = userName,
                    email = email,
                    password = password
                )

                // Save to local database
                userDao.insertUser(userEntity)

                // Also save to Firebase for backup
                try {
                    val userMap = userEntity.toMap()
                    fireStore.collection("users").document(userId).set(userMap).await()
                    emit(Resource.Success(user))
                } catch (exception: Exception) {
                    // Even if Firebase save fails, we still have local data
                    emit(Resource.Success(user))
                }
            } ?: emit(Resource.Error("Registration failed: User is null"))

        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun loginUser(email: String, password: String): Flow<Resource<FirebaseUser?>> =
        flow {
            emit(Resource.Loading)
            try {
                val authResult = suspendCoroutine<AuthResult> { continuation ->
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->
                            continuation.resume(result)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }

                val user = authResult.user
                if (user == null) {
                    emit(Resource.Error("Login failed: User is null"))
                    return@flow
                }

                // Check local database first
                val localUser = userDao.getUserById(user.uid)
                if (localUser != null) {
                    emit(Resource.Success(user))
                    return@flow
                }

                // If not in local DB, try to get from Firebase and save locally
                try {
                    val userDoc = fireStore.collection("users").document(user.uid).get().await()
                    if (userDoc.exists()) {
                        val userEntity = UserEntity(
                            userId = user.uid,
                            username = userDoc.getString("username") ?: "",
                            email = userDoc.getString("email") ?: "",
                            password = userDoc.getString("password") ?: ""
                        )
                        userDao.insertUser(userEntity)
                        emit(Resource.Success(user))
                    } else {
                        emit(Resource.Error("User not found in database"))
                    }
                } catch (e: Exception) {
                    emit(Resource.Error("Failed to fetch user data: ${e.message}"))
                }

            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Authentication failed"))
            }
        }
}

fun UserEntity.toMap(): Map<String, Any> {
    return mapOf(
        "userId" to userId,
        "username" to username,
        "email" to email,
        "password" to password
    )
}
