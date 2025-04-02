package com.example.AllGpt.presentation.LoginOrRegisterLayout

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.AllGpt.R
import com.example.AllGpt.databinding.ActivityLoginBinding
import com.example.AllGpt.presentation.AuthenticationViewModel
import com.example.AllGpt.presentation.HomePageLayout.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupLoginButton()
    }
    private fun setupLoginButton(){
        binding.loginButton.setOnClickListener {
            val email = binding.Email.text.toString().trim()
            val password = binding.Password.text.toString().trim()
            if (validateInputs(email, password)) {
                viewModel.loginUser( email, password)
            }
        }
        observeLoginState()
    }

    private fun validateInputs(email : String, password : String) : Boolean{
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        if (email.isEmpty() || !emailRegex.matches(email)) {
            binding.Email.error = "Invalid email address"
            return false
        }
        if (password.isEmpty()) {
            binding.Password.error = "Password is empty"
            return false
        }
        return true
    }


    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is AuthenticationViewModel.LoginState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.loginButton.isEnabled = false
                    }

                    is AuthenticationViewModel.LoginState.Success -> {
                        binding.progressBar.isVisible = false
                        val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }

                    is AuthenticationViewModel.LoginState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.loginButton.isEnabled = true
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG)
                            .show()
                    }

                    is AuthenticationViewModel.LoginState.Idle -> {
                        binding.progressBar.isVisible = false
                        binding.loginButton.isEnabled = true
                    }
                }
            }
        }
    }
}