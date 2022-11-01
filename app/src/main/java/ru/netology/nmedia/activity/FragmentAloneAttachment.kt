package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAloneAttachmentBinding
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FragmentAloneAttachment : Fragment() {

    private val viewModel: PostViewModel  by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentAloneAttachmentBinding.inflate(inflater, container, false)

        val post = viewModel.openedAttachment.value

        post?.let {
            if (!it.attachment?.url.isNullOrBlank()) {
                Glide.with(binding.attachmentContent)
                    .load(it.attachment?.url)
                    .placeholder(R.drawable.ic_avatar_empty_48dp)
                    .error(R.drawable.ic_avatar_empty_48dp)
                    .timeout(10_000)
                    .into(binding.attachmentContent)
            }

            viewModel.onCancelOpenAttachment()
        }

        return binding.root

    }

}