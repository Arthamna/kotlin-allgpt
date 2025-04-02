package com.example.AllGpt.presentation.LoginOrRegisterLayout

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.AllGpt.R
import com.example.AllGpt.databinding.ActivityLoginRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginOrRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRegisterBinding
//    private val viewModel : AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.registerButton.setOnClickListener{
            val intent= Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.signButton.setOnClickListener{
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}