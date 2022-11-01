package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostInteractionCommands
import ru.netology.nmedia.databinding.CardPostBinding

class PostAdapter(private val interactionCommands: PostInteractionCommands) : PagingDataAdapter<Post, PostViewHolder>(
    PostDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, interactionCommands)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        getItem(position)?.let { post ->
            holder.bind(post)
        }

    }

}