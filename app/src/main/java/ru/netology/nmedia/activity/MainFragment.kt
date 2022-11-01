package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentMainBinding
import ru.netology.nmedia.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentMainBinding.inflate(inflater, container, false)

        val adapter =  PostAdapter(viewModel)

        viewModel.onCancelEdit()
        viewModel.onCancelOpen()

        binding.listOfPosts.adapter = adapter

        //viewModel.loadPosts()

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest { state ->
                adapter.submitData(state)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->

            binding.retryGroup.isGone = !state.error
            //binding.retryGroup.isGone = true
            binding.progress.isGone = !state.loading
            binding.postsGroup.isGone = (state.error || state.loading)
            //binding.postsGroup.isGone = (state.loading)
            binding.swipeRefreshOfPosts.isRefreshing = state.refreshing

        }

        binding.swipeRefreshOfPosts.setOnRefreshListener{
            adapter.refresh()
            binding.swipeRefreshOfPosts.isRefreshing = false
        }

        viewModel.postUpdated.observe(viewLifecycleOwner) { post ->
            viewModel.updatedPost(post)
        }

        viewModel.never.observe(viewLifecycleOwner) { countNeverPosts ->
            println(countNeverPosts)
        }

        binding.retryButton.setOnClickListener {
            adapter.refresh()
        }

        binding.fabNewPost.setOnClickListener {
            viewModel.onCancelEdit()
            findNavController().navigate(R.id.action_main_fragment_to_edit_post_fragment)
        }


        viewModel.editedPost.observe(viewLifecycleOwner) { post ->
            if (post.id != 0L) {
                findNavController().navigate(R.id.action_main_fragment_to_edit_post_fragment)
            }
        }

        viewModel.openedPost.observe(viewLifecycleOwner) { post ->
            if (post.id != 0L) {
                findNavController().navigate(R.id.action_nav_main_fragment_to_alone_post_fragment)
            }
        }

        viewModel.openedAttachment.observe(viewLifecycleOwner) { post ->
            if (!post.attachment?.url.isNullOrBlank()) {
                findNavController().navigate(R.id.action_nav_main_fragment_to_attachment_alone_fragment)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { textMessage ->

            if (textMessage.isNotBlank()) {
                val textEnd = " Попробуйте повторить попытку позже."
                val toast = Toast.makeText(context, textMessage + textEnd, Toast.LENGTH_LONG)
                toast.show()
                viewModel.errorMessage.value = ""
            }
        }

        viewModel.never.observe(viewLifecycleOwner) { countNeverPosts ->
            binding.buttonReadNeverPosts.isGone = countNeverPosts <= 0
            val textReadNeverPosts = getText(R.string.read_never_posts)
            binding.buttonReadNeverPosts.text = "$textReadNeverPosts (" + countNeverPosts.toString() + ")"
        }

        binding.buttonReadNeverPosts.setOnClickListener {
            adapter.refresh()
        }

        viewModel.needScrolling.observe(viewLifecycleOwner) {
            binding.listOfPosts.smoothScrollToPosition(0)
        }

        return binding.root

    }

    companion object {

        fun newInstance() = MainFragment()

    }

}