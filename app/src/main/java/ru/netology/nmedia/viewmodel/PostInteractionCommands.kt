package ru.netology.nmedia.viewmodel

import ru.netology.nmedia.dto.Post

interface PostInteractionCommands {

    fun onLike(post: Post)

    fun onShare(post: Post)

    fun onRemove(post: Post)

    fun onEditPost(post: Post)

    fun onOpenPost(post: Post)

    fun onOpenAttachment(post: Post)

    fun onSaveContent(newContent: String)

    fun onCancelEdit()

    fun onCancelOpen()

    fun onCancelOpenAttachment()

    fun readNeverPosts()


}
