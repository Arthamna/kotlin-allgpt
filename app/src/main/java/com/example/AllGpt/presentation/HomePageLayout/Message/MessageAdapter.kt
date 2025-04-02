package com.example.AllGpt.presentation.HomePageLayout.Message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.AllGpt.databinding.MessageAiBinding
import com.example.AllGpt.databinding.MessageUserBinding
import com.example.AllGpt.domain.model.MessageEntity

class MessageAdapter : ListAdapter<MessageEntity, RecyclerView.ViewHolder>(MessageDiffUtil()) {
    companion object {
        const val TYPE_USER = 0
        const val TYPE_AI = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_USER -> UserMessageViewHolder(MessageUserBinding.inflate(inflater, parent, false))
            else -> AIMessageViewHolder(MessageAiBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is AIMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isFromUser) TYPE_USER else TYPE_AI
    }

    class UserMessageViewHolder(private val binding: MessageUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageEntity) {
            binding.textMessage.text = message.content
            binding.timestamp.text = message.timestamp
        }
    }

    class AIMessageViewHolder(private val binding: MessageAiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageEntity) {
            binding.textMessage.text = message.content
            binding.timestamp.text = message.timestamp
        }
    }

    class MessageDiffUtil : DiffUtil.ItemCallback<MessageEntity>() {
        override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity) =
            oldItem.messageId == newItem.messageId

        override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity) =
            oldItem == newItem
    }
}