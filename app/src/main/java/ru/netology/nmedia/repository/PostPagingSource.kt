package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dto.Post

class PostPagingSource (
    private val postsApiService : PostsApiService
        ) : PagingSource<Long, Post>() {

    override fun getRefreshKey(state: PagingState<Long, Post>): Long?  = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {

        try {
            val result =
                when (params) {
                    is LoadParams.Refresh -> postsApiService.getLatest(params.loadSize)
                    is LoadParams.Append -> postsApiService.getBefore(params.key, params.loadSize)
                    is LoadParams.Prepend -> return LoadResult.Page(
                        data = emptyList(), nextKey = null, prevKey = params.key
                    )
                    else -> error("Not found params type in klass PostPagingSource")
                }

            if (!result.isSuccessful) {
                error("Response code: ${result.code()}")
            }

            val data = result.body().orEmpty()
            return LoadResult.Page(
                data = data,
                prevKey = params.key,
                nextKey = data.lastOrNull()?.id
            )
        }
        catch (e: Exception) {
            return LoadResult.Error(e)
        }

    }

}