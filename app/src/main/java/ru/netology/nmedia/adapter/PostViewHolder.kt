package ru.netology.nmedia.adapter

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostInteractionCommands
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

class PostViewHolder (private val binding: CardPostBinding, private val interactiveCommands: PostInteractionCommands) : RecyclerView.ViewHolder(binding.root){

    fun bind (post: Post) = with(binding) {
        textViewAuthorName.text = post.author
        textViewAuthorDate.text = Date(post.published * 1000).toString()
        textViewContent.text = post.content
        buttonFavorite.text = compactDecimalFormat(post.likes)
        buttonShare.text = compactDecimalFormat(post.shares)
        buttonVisibility.text = compactDecimalFormat(post.visibilities)

        if (post.video.isNullOrBlank()) {
            videoContent.visibility = View.GONE
            fabPlay.visibility = View.GONE
        } else {
            videoContent.setImageURI(Uri.parse(post.video))
        }

        if (post.attachment == null) {
            attachmentContent.visibility = View.GONE
        } else {
            Glide.with(binding.attachmentContent)
                .load(post.attachment?.url)
                .placeholder(R.drawable.ic_avatar_empty_48dp)
                .error(R.drawable.ic_avatar_empty_48dp)
                .timeout(10_000)
                .into(binding.attachmentContent)
        }

        if (post.authorAvatar.isNotBlank()) {
            Glide.with(binding.imageViewAvatar)
                .load(post.authorAvatar)
                .placeholder(R.drawable.ic_avatar_empty_48dp)
                .error(R.drawable.ic_avatar_empty_48dp)
                .timeout(10_000)
                .circleCrop()
                .into(binding.imageViewAvatar)
        }

        buttonFavorite.isChecked =  post.likedByMe

        buttonFavorite.setOnClickListener {
            buttonFavorite.isChecked = !buttonFavorite.isChecked
            interactiveCommands.onLike(post)
        }

        buttonShare.setOnClickListener {

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(intent, R.string.chooser_share_post.toString())
            ContextCompat.startActivity(root.context, shareIntent, null)

            interactiveCommands.onShare(post)
        }

        fabPlay.setOnClickListener {
            openVideo(this, post)
        }

        videoContent.setOnClickListener {
            openVideo(this, post)
        }

        imageButtonMore.isVisible = post.ownedByMe

        imageButtonMore.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options_post)

                setOnMenuItemClickListener { menuItem ->
                    when(menuItem.itemId) {
                        R.id.remove_button -> {
                            interactiveCommands.onRemove(post)
                            true
                        }
                        R.id.edit_button -> {
                            interactiveCommands.onEditPost(post)

                            true
                        }
                        else -> false
                    }
                }

            }.show()
        }

        postMainLayout.setOnClickListener {

            interactiveCommands.onOpenPost(post)

        }

        attachmentContent.setOnClickListener{
            interactiveCommands.onOpenAttachment(post)
        }

    }

    fun openVideo(binding: CardPostBinding, post: Post)  {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
        val shareIntent = Intent.createChooser(intent, R.string.text_open_url.toString())
        ContextCompat.startActivity(binding.root.context, shareIntent, null)
    }

    private fun compactDecimalFormat(number: Int): String {
        val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')

        val numValue = number.toLong()
        val value = floor(log10(numValue.toDouble())).toInt()
        val base = value / 3

        return if (value >= 3 && base < suffix.size) {
            DecimalFormat("#0.0").format(
                numValue / 10.0.pow((base * 3).toDouble())
            ) + suffix[base]
        } else {
            DecimalFormat("#,##0").format(numValue)
        }
    }

}
