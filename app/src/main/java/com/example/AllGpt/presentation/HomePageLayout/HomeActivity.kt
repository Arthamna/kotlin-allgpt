package com.example.AllGpt.presentation.HomePageLayout

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.AllGpt.R
import com.example.AllGpt.databinding.ActivityHomeBinding
import com.example.AllGpt.domain.model.TopicEntity
import com.example.AllGpt.presentation.AuthenticationViewModel
import com.example.AllGpt.presentation.HomePageLayout.Message.MessageFragment
import com.example.AllGpt.presentation.HomePageLayout.Topic.TopicAdapter
import com.example.AllGpt.presentation.HomePageLayout.Topic.TopicViewModel
import com.example.AllGpt.presentation.LoginOrRegisterLayout.LoginOrRegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private val authViewModel: AuthenticationViewModel by viewModels()
    private val topicViewModel: TopicViewModel by viewModels()
    private lateinit var topicAdapter: TopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        observeAuthState()
        setupTopicsRecyclerView()
        setupToolbar()
        observeTopics()
        setupTopicInput()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MessageFragment())
                .commitNow()
            Log.d("FragmentCheck", "MessageFragment ditambahkan ke fragment_container")
        }
    }

    private fun setupTopicInput() {
        val navHeaderBinding = binding.navHeader
//        Log.d("TopicDebug", "Setting up topic input...")
        val addTopicBar = navHeaderBinding.root.findViewById<EditText>(R.id.add_topic_bar)
        val addTopicIcon = navHeaderBinding.root.findViewById<ImageButton>(R.id.add_topic_icon)

        addTopicBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                addTopicIcon.visibility = if (!s.isNullOrBlank()) View.VISIBLE else View.GONE
            }
        })

        fun handleTopicCreation(topicTitle: String) {
            if (topicTitle.isNotEmpty()) {
                topicViewModel.createNewTopic(topicTitle)
                addTopicBar.text.clear()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MessageFragment())
                    .commit()
            }
        }

        addTopicIcon.setOnClickListener {
            val topicTitle = addTopicBar.text.toString().trim()
            handleTopicCreation(topicTitle)
        }

        addTopicBar.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {

                val topicTitle = addTopicBar.text.toString().trim()
                handleTopicCreation(topicTitle)
            }
            false
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_topic)
        }
    }

    private fun setupTopicsRecyclerView() {
        val recyclerView = binding.navView.findViewById<RecyclerView>(R.id.chat_topics_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        topicAdapter = TopicAdapter().apply {
            setOnTopicClickListener { topic ->
                handleTopicSelection(topic)
            }
        }
        recyclerView.adapter = topicAdapter
    }
//harus dicek topic ada/tidak.
    private fun handleTopicSelection(topic: TopicEntity) {
        topicViewModel.selectTopic(topic)

        val fragment = MessageFragment().apply {
            arguments = bundleOf("topicId" to topic.topicId)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun observeTopics() {
        lifecycleScope.launch {
            topicViewModel.topicList.collectLatest { topics ->
                topicAdapter.submitList(topics)
            }
        }

        lifecycleScope.launch {
            topicViewModel.currentTopic.collectLatest { topic ->
                topic?.let { updateTopicUI(it) }
            }
        }
    }

    private fun updateTopicUI(topic: TopicEntity) {
        supportActionBar?.title = topic.title
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.chat_menu, menu)
            return true
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {  // KIRI
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            R.id.logout -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                authViewModel.logOut()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                when (state) {
                    is AuthenticationViewModel.AuthState.SignedOut -> {
                        val intent = Intent(this@HomeActivity, LoginOrRegisterActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                    else -> {}
                }
            }
        }
    }
}