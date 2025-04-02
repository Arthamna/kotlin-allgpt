package com.example.AllGpt.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.AllGpt.domain.use_case.AuthenticationUseCase
import com.example.AllGpt.domain.use_case.ChatUseCase
import com.example.AllGpt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authUseCase: AuthenticationUseCase,
    private val chatUseCase: ChatUseCase,
    private val user: FirebaseAuth
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState = _registerState.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()


    fun registerUser(userName: String, email: String, password: String) = viewModelScope.launch {
        _registerState.value = RegisterState.Loading
        try {
            authUseCase.registerUser(userName, email, password).collect { result ->
                _registerState.value = when(result) {
                    is Resource.Success -> RegisterState.Success(result.data)
                    is Resource.Error -> RegisterState.Error(result.message ?: "Unknown Regist error")
                    is Resource.Loading -> RegisterState.Loading
                }
            }
        } catch(e: Exception) {
            _registerState.value = RegisterState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun logOut() = viewModelScope.launch {
        try {
            val userId =  user.currentUser?.uid
            if (userId != null) {
                chatUseCase.clearUserData(userId)
            }
            FirebaseAuth.getInstance().signOut()
            _authState.value = AuthState.SignedOut
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Sign out failed")
            Log.e("AuthViewModel", "Error during logout", e)
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
        try {
            authUseCase.loginUser(email, password).collect {result ->
            _loginState.value = when (result) {
                    is Resource.Success -> LoginState.Success(result.data)
                    is Resource.Error -> LoginState.Error(result.message ?: "Unknown Login error")
                    is Resource.Loading -> LoginState.Loading
            }
        }
    } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }


    sealed class AuthState {
        object Idle : AuthState()
        object SignedOut : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val user: FirebaseUser?) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: FirebaseUser?) : LoginState()
        data class Error(val message: String) : LoginState()
    }

}

