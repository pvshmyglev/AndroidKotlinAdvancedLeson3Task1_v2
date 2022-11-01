package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.util.hideKeyboard

@AndroidEntryPoint
class EditPostFragment : Fragment() {

    companion object {
        private const val MAX_IMAGE_SIZE = 2048
    }

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val menuHost: MenuHost = requireActivity()

        val binding = FragmentEditPostBinding.inflate(inflater, container, false)

        val imageContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), "Error read file", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    val data = result.data?.data ?: run {
                        Toast.makeText(requireContext(), "Error read file", Toast.LENGTH_SHORT)
                            .show()
                        return@registerForActivityResult
                    }
                    viewModel.saveAttachment(data, data.toFile())
                }
            }
        }

        binding.textNewPost.requestFocus()

        binding.textNewPost.setText(viewModel.editedPost.value?.content)

        binding.buttonClearPreview.isGone = true

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.item_new_post_save -> {

                        if (binding.textNewPost.text.isNotBlank()) {
                            viewModel.onSaveContent(binding.textNewPost.text.toString())
                            requireView().hideKeyboard()
                        }
                        findNavController().navigateUp()

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.buttonNewPostInsertFile.setOnClickListener{
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .maxResultSize(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
                .createIntent(imageContract::launch)

        }

        binding.buttonNewPostInsertPhoto.setOnClickListener{
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                .maxResultSize(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
                .createIntent(imageContract::launch)

        }

        viewModel.media.observe(viewLifecycleOwner) {

            if(it?.uri == null ) {
                binding.imagePreview.isGone = true
                binding.buttonClearPreview.isGone = true
                return@observe
            }

            binding.imagePreview.isVisible = true
            binding.imagePreview.setImageURI(it.uri)
            binding.buttonClearPreview.isVisible = true

        }

        binding.buttonClearPreview.setOnClickListener{
            viewModel.clearAttachment()
        }

        return binding.root

    }

}