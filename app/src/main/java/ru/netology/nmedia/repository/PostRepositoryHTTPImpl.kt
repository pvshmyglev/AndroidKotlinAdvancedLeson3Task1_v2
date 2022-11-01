package ru.netology.nmedia.repository

import androidx.paging.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.api.toPostWithFullUrl
import ru.netology.nmedia.api.toPostsWithFullUrl
import ru.netology.nmedia.auth.LoginUser
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.*
import ru.netology.nmedia.entity.toPostEntity
import ru.netology.nmedia.entity.toReadedPostEntity
import ru.netology.nmedia.entity.toReadedPostsEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.exception.AppError
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryHTTPImpl @Inject constructor(
    private val postDao: PostDao,
    private val postApiService: PostsApiService,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(initialLoadSize = 15, pageSize = 10),
        remoteMediator = PostRemoteMediator(postApiService, appDb, postDao, postRemoteKeyDao),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toPost)
    }


    override suspend fun getAll() {

        val result = postApiService.getAll()

        if (!result.isSuccessful) {
            error("Response code: ${result.code()}")
        }

        val postsOnServer = result.body() ?: error("Body is null")
        val posts = postsOnServer.toPostsWithFullUrl()
        postDao.insert(posts.toPostEntity())
        postDao.insertReadingPosts(posts.toReadedPostEntity())

    }

    override fun getNeverCount(): Flow<Int> = flow {
        /*
        while (true) {

            val result = postApiService.getNewer(postDao.getMaxId())
            if (!result.isSuccessful) {
                error("Response code: ${result.code()}")
            }
            val postsOnServer = result.body() ?: error("Body is null")
            val posts = postsOnServer.toPostsWithFullUrl()
            postDao.insert(posts.toPostEntity())

            val countResult = postDao.getNeverCount()

            emit(countResult)
            delay(10_000)
        }
        */
        emit(0)

    }
        .catch { e ->
            val err = AppError.from(e)
            throw CancellationException()
        }
        .flowOn(Dispatchers.Default)

    override suspend fun getById(id: Long): Post {

        val result = postApiService.getById(id)

        if (!result.isSuccessful) {
            error("Response code: ${result.code()}")
        }

        val postOnServer = result.body() ?: error("Body is null")
        val post = postOnServer.toPostWithFullUrl()
        postDao.insertPost(post.toPostEntity())

        return post

    }

    override suspend fun readAllPosts() {
        postDao.insertReadingPosts(
            postDao.getUnreadedPosts().toReadedPostsEntity()
        )
        getNeverCount()
    }

    override suspend fun loginAsUser(loginUser: LoginUser) : AuthState {

        val result = postApiService.loginAsUser(loginUser.login, loginUser.password)

        if (!result.isSuccessful) {
            error("Response code: ${result.code()}")
        }

        val authState = result.body() ?: error("Body is null")

        return authState

    }

    override suspend fun likeById(id: Long) {

        val postInBase = postDao.getById(id)
        val likedPost = postInBase.copy(
            likedByMe = !postInBase.likedByMe,
            likes = if (postInBase.likedByMe) {postInBase.likes - 1} else {postInBase.likes + 1}
        )

        postDao.insertPost(likedPost)

        val result =
            if (postInBase.likedByMe) { postApiService.dislikeById(id) } else { postApiService.likeById(id) }
        if (!result.isSuccessful) {
            error("Response code: ${result.code()}")
        }
    }

    override suspend fun shareById(id: Long) {

    }

    override suspend fun removeById(id: Long) {
        postDao.removeById(id)
        val result = postApiService.removeById(id)
        if (!result.isSuccessful) {
            error("Response code: ${result.code()}")
        }

    }

    override suspend fun save(post: Post) {
        val result = postApiService.save(post)
        if (!result.isSuccessful) {
            error("Response code: ${result.code()}")
        }
        val refreshPostOnServer = result.body() ?: error("Body is null")
        val refreshPost = refreshPostOnServer.toPostWithFullUrl()
        postDao.insertPost(refreshPost.toPostEntity())
        postDao.insertReadingPost(refreshPost.toReadedPostEntity())
    }

    override suspend fun saveWithAttachment(post: Post, file: File?) {
        if (file == null) return save(post)

        val media = upload(file)
        val result = postApiService.save(
            post.copy(
                attachment = Attachment(
                    url = media.id,
                    description = "",
                    type = AttachmentType.IMAGE)
            )
        )
        if (!result.isSuccessful) {
            error("Response code: ${result.code()}")
        }
        val refreshPostOnServer = result.body() ?: error("Body is null")
        val refreshPost = refreshPostOnServer.toPostWithFullUrl()

        postDao.insertPost(refreshPost.toPostEntity())
        postDao.insertReadingPost(refreshPost.toReadedPostEntity())
    }

    private suspend fun upload(file: File) : Media {
        try {

            val media = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
            val result = postApiService.upload(media)

            if (!result.isSuccessful) {
                error("Response code: ${result.code()}")
            }

            return result.body() ?: error("Body is null")

        } catch (e: Exception) {
            val err = AppError.from(e)
            error(err)
        }
    }


}