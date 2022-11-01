package ru.netology.nmedia.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.enumeration.AttachmentType

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "authorId")
    val authorId: Long,
    @ColumnInfo(name = "authorAvatar")
    val authorAvatar: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "publishedDate")
    val published: Long,
    @ColumnInfo(name = "likeByMe")
    val likedByMe: Boolean = false,
    @ColumnInfo(name = "countLikes")
    val likes: Int = 0,
    @Embedded
    var attachment: AttachmentEmbeddable? = null,

    )

@Entity(tableName = "readedposts")
data class ReadedPostsEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long,
)

data class AttachmentEmbeddable(
    @ColumnInfo(name = "url")
    var url: String,
    @ColumnInfo(name = "description")
    var description: String?,
    @ColumnInfo(name = "type")
    var type: AttachmentType,
)