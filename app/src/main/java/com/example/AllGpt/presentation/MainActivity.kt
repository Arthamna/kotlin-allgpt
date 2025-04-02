package com.example.AllGpt.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.AllGpt.databinding.ActivityMainBinding
import com.example.AllGpt.presentation.HomePageLayout.HomeActivity
import com.example.AllGpt.presentation.LoginOrRegisterLayout.LoginOrRegisterActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        lifecycleScope.launch {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                navigateToHome()
            } else {
                navigateToLoginRegister()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginRegister() {
        val intent = Intent(this, LoginOrRegisterActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}























