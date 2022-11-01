package ru.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.ReadedPostsEntity

@Dao
interface PostDao {

    @Query("SELECT posts.* FROM posts INNER JOIN readedposts ON posts.id = readedposts.id ORDER BY posts.id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts ORDER BY posts.id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getById(id: Long) : PostEntity

    @Query("SELECT MAX(id) FROM posts")
    suspend fun getMaxId() : Long

    @Query("SELECT COUNT(posts.id) FROM posts LEFT JOIN readedposts ON posts.id = readedposts.id WHERE readedposts.id IS NULL")
    suspend fun getNeverCount() : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingPosts(posts: List<ReadedPostsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingPost(post: ReadedPostsEntity)

    @Query("SELECT posts.* FROM posts LEFT JOIN readedposts ON posts.id = readedposts.id WHERE readedposts.id IS NULL ORDER BY posts.id DESC")
    suspend fun getUnreadedPosts() : List<PostEntity>

    @Query("DELETE FROM posts")
    suspend fun removeAll()

    suspend fun save(post: PostEntity): PostEntity {

        return if (post.id == 0L) {

            val newId = insertPost(post)

            post.copy(id = newId)

        } else {

            updatePost(post)

            post

        }

    }

}