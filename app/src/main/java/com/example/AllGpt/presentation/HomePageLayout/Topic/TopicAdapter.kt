package com.example.AllGpt.presentation.HomePageLayout.Topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.AllGpt.databinding.ItemTopicBinding
import com.example.AllGpt.domain.model.TopicEntity

class TopicAdapter : ListAdapter<TopicEntity, TopicAdapter.TopicViewHolder>(TopicDiffCallback()) {
    private var onTopicClickListener: ((TopicEntity) -> Unit)? = null

    fun setOnTopicClickListener(listener: (TopicEntity) -> Unit) {
        onTopicClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TopicViewHolder(
        private val binding: ItemTopicBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: TopicEntity) {
            binding.apply {
                topicTitle.text = topic.title
                topicLastMessage.text = topic.lastMessage ?: "No messages yet"
                topicTimestamp.text = topic.lastSyncTimestamp

                root.setOnClickListener {
                    onTopicClickListener?.invoke(topic)
                }
            }
        }
    }

    class TopicDiffCallback : DiffUtil.ItemCallback<TopicEntity>() {
        override fun areItemsTheSame(oldItem: TopicEntity, newItem: TopicEntity): Boolean {
            return oldItem.topicId == newItem.topicId
        }

        override fun areContentsTheSame(oldItem: TopicEntity, newItem: TopicEntity): Boolean {
            return oldItem == newItem
        }
    }
}