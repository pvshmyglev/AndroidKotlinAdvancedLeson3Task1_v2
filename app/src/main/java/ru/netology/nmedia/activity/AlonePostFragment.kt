package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding

@AndroidEntryPoint
class AlonePostFragment : Fragment() {

    private val viewModel: PostViewModel  by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = CardPostBinding.inflate(inflater, container, false)

        viewModel.openedPost.observe(viewLifecycleOwner) { post ->

            if (post.id != 0L) {

                viewModel.openedPost.value?.apply {

                    PostViewHolder(binding, viewModel).bind(this)
                }

            } else {

                findNavController().navigate(R.id.action_nav_alone_post_fragment_to_nav_main_fragment)

            }

        }

        viewModel.editedPost.observe(viewLifecycleOwner) { post ->
            if (post.id != 0L) {
                findNavController().navigate(R.id.action_nav_alone_post_fragment_to_nav_edit_post_fragment)
            }
        }

        viewModel.openedAttachment.observe(viewLifecycleOwner) { post ->
            if (!post.attachment?.url.isNullOrBlank()) {
                findNavController().navigate(R.id.action_nav_alone_post_fragment_to_nav_attachment_alone_fragment)
            }
        }

        return binding.root

    }

}