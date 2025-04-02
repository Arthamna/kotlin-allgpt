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
import com.example.AllGpt.databinding.ActivityRegisterBinding
import com.example.AllGpt.presentation.AuthenticationViewModel
import com.example.AllGpt.presentation.HomePageLayout.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupRegisterButton()
        observeRegisterState()
    }
    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            val userName = binding.UserName.text.toString().trim()
            val email = binding.Email.text.toString().trim()
            val password = binding.Password.text.toString().trim()
            val confirmPassword = binding.ConfirmPassword.text.toString().trim()

            if (validateInputs(userName, email, password, confirmPassword)) {
                viewModel.registerUser(userName, email, password)
            }
        }
    }
    private fun validateInputs(userName: String, email: String, password: String, confirmPassword: String): Boolean {

        val nameRegex = Regex("^(?=.*[a-z])(?=.*[A-Z]).+$")
        if (userName.isEmpty() || !nameRegex.matches(userName)) {
            binding.UserName.error = "Name must contain at least one uppercase and one lowercase letter"
            return false
        }
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        if (email.isEmpty() || !emailRegex.matches(email)) {
            binding.Email.error = "Invalid email address"
            return false
        }
        if (password.isEmpty() || password.length < 8) {
            binding.Password.error = "Password must be at least 8 characters"
            return false
        }
        if (password != confirmPassword) {
            binding.ConfirmPassword.error = "Passwords don't match"
            return false
        }
        return true
    }
    private fun observeRegisterState() {
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when(state) {
                    is AuthenticationViewModel.RegisterState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.registerButton.isEnabled = false
                    }
                    is AuthenticationViewModel.RegisterState.Success -> {
                        binding.progressBar.isVisible = false
                        val intent = Intent(this@RegisterActivity, HomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                    is AuthenticationViewModel.RegisterState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.registerButton.isEnabled = true
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    is AuthenticationViewModel.RegisterState.Idle -> {
                        binding.progressBar.isVisible = false
                        binding.registerButton.isEnabled = true
                    }
                }
            }
        }
    }
}