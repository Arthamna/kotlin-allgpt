package com.example.AllGpt.presentation.HomePageLayout.Message

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.AllGpt.R
import com.example.AllGpt.databinding.DialogNewTopicBinding
import com.example.AllGpt.databinding.FragmentMessageBinding
import com.example.AllGpt.presentation.HomePageLayout.Topic.TopicViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MessageFragment : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private val messageViewModel: MessageViewModel by viewModels()
    private val messageAdapter = MessageAdapter()
    private val topicViewModel: TopicViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupMessageInput()
        setupSendButton()

        initializeTopicAndMessages()
    }

    private fun initializeTopicAndMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            arguments?.getInt("topicId", -1)?.let { topicId ->
                if (topicId != -1) {
                    topicViewModel.topicList.collect { topics ->
                        topics.find { it.topicId == topicId }?.let { topic ->
                            topicViewModel.selectTopic(topic)
                            return@collect
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                topicViewModel.currentTopic.collect { currentTopic ->
                    if (currentTopic != null) {
                        try {
                            messageViewModel.messages.collect { messages ->
                                _binding?.let { binding ->
                                    messageAdapter.submitList(messages) {
                                        if (messages.isNotEmpty()) {
                                            binding.messageRecyclerView.smoothScrollToPosition(messages.size - 1)
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MessageFragment","Error collect message")
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.messageRecyclerView.apply {
            setHasFixedSize(true)
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
                reverseLayout = false
            }
        }
    }

    private fun setupMessageInput() {
        binding.messageInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.sendButton.visibility = if (s.isNullOrBlank()) View.GONE else View.VISIBLE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val messageText = binding.messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    if (messageViewModel.currentTopic.value == null) {
//                        showCreateTopicDialog(messageText)
                        topicViewModel.createNewTopic(messageText)
                        topicViewModel.currentTopic.first { it != null }?.let {
                            messageViewModel.sendMessage(messageText)
                            _binding?.messageInput?.text?.clear()
                        }
                    } else {
                        messageViewModel.sendMessage(messageText)
                        binding.messageInput.text.clear()
                    }
                }
            }
        }
    }

//    private fun showCreateTopicDialog(pendingMessage: String) {
//        val dialogBinding = DialogNewTopicBinding.inflate(layoutInflater)
//        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialDialog)
//            .setTitle("New Chat Topic")
//            .setView(dialogBinding.root)
//            .create()
//
//        val positiveButton = dialogBinding.root.findViewById<Button>(R.id.buttonCreate)
//        val negativeButton = dialogBinding.root.findViewById<Button>(R.id.buttonCancel)
//
//        positiveButton.setOnClickListener {
//            val title = dialogBinding.editTextTopicTitle.text.toString()
//            if (title.isNotEmpty()) {
//                viewLifecycleOwner.lifecycleScope.launch {
//                    try {
//                        topicViewModel.createNewTopic(title)
//                        // Wait for the topic to be created and set as current
//                        topicViewModel.currentTopic.first { it != null }?.let {
//                            messageViewModel.sendMessage(pendingMessage)
//                            _binding?.messageInput?.text?.clear()
//                        }
//                    } catch (e: Exception) {
//                        Log.e("MessageFragment", "Error creating topic", e)
//                    }
//                }
//            }
//            dialog.dismiss()
//        }

//        negativeButton.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}