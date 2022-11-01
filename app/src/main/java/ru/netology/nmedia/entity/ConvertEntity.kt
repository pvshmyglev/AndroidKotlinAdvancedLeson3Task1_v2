package ru.netology.nmedia.entity

import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

internal fun List<PostEntity>.toPost() = map(PostEntity::toPost)
internal fun List<Post>.toPostEntity() = map(Post::toPostEntity)
internal fun List<Post>.toReadedPostEntity() = map(Post::toReadedPostEntity)
internal fun List<PostEntity>.toReadedPostsEntity() = map(PostEntity::toReadedPostEntity)

internal fun PostEntity.toPost() = Post (

    id = id,
    author = author,
    authorId = authorId,
    authorAvatar = authorAvatar,
    content = content,
    video = "",
    published = published,
    likedByMe = likedByMe,
    likes = likes,
    shares = 0,
    visibilities = 0,
    attachment = attachment?.toAttachment()

)

internal fun Post.toPostEntity() = PostEntity (

    id = id,
    author = author,
    authorId = authorId,
    authorAvatar = authorAvatar,
    content = content,
    published = published,
    likedByMe = likedByMe,
    likes = likes,
    attachment = attachment?.toAttachmentEmbeddable()
)

internal fun Attachment.toAttachmentEmbeddable() = AttachmentEmbeddable (

    url = url,
    description = description,
    type = type,

)

internal fun AttachmentEmbeddable.toAttachment() = Attachment (

    url = url,
    description = description,
    type = type,

)

internal fun Post.toReadedPostEntity() = ReadedPostsEntity (id = id)
internal fun PostEntity.toReadedPostEntity() = ReadedPostsEntity (id = id)