package ru.netology.nmedia.api

import ru.netology.nmedia.dto.Post

private const val BASE_URL_MEDIA = "http://10.0.2.2:9999/media/"
private const val BASE_URL_AVATARS = "http://10.0.2.2:9999/avatars/"

internal fun List<Post>.toPostsWithFullUrl() = map(Post::toPostWithFullUrl)

internal fun Post.toPostWithFullUrl() =

    if (attachment == null && authorAvatar.isNullOrBlank()) {
       this
    } else {
       val newAttachment = attachment?.copy(url = BASE_URL_MEDIA + attachment?.url)
       val newAuthorAvatar = BASE_URL_AVATARS + authorAvatar
        copy(attachment = newAttachment, authorAvatar = newAuthorAvatar)
    }
