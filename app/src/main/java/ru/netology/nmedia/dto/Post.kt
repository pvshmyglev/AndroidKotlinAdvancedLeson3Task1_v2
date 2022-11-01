package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(

    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String,
    val content: String,
    val video: String?,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int,
    val shares: Int,
    val visibilities: Int,
    val ownedByMe: Boolean = false,
    var attachment: Attachment? = null,
    )

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)