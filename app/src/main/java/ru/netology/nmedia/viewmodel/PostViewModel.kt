package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.MediaModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor (
    private val repository: PostRepository,
    private val auth: AppAuth,
) : ViewModel(), PostInteractionCommands {

    private val emptyAttachment = MediaModel()

    val data : Flow<PagingData<Post>> = auth.data.flatMapLatest { (id, _) ->
        repository.data.map { posts ->
            posts.map {
                it.copy(ownedByMe = it.authorId == id)
            }
        }
    }.flowOn(Dispatchers.Default)

    private val _media = MutableLiveData<MediaModel>()
    val media : LiveData<MediaModel>
        get() = _media

    private val _state = MutableLiveData(FeedModelState())
    val state : LiveData<FeedModelState>
        get() = _state

    val never: LiveData<Int> = repository.getNeverCount().asLiveData(Dispatchers.Default)

    private val emptyPost = Post(
        0L,
        "",
        0L,
        "",
        "",
        "",
        0,
        false,
        0,
        0,
        0
    )

    val editedPost = MutableLiveData(emptyPost)
    val openedPost = MutableLiveData(emptyPost)
    val openedAttachment = MutableLiveData(emptyPost)

    private val _postUpdated = SingleLiveEvent<Post>()
    val postUpdated: LiveData<Post>
        get() = _postUpdated

    private val _needScrolling = SingleLiveEvent<Boolean>()
    val needScrolling: LiveData<Boolean>
        get() = _needScrolling

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: MutableLiveData<String>
        get() = _errorMessage

    fun updatedPost(post: Post) {

        viewModelScope.launch {

            try {
                _state.value = FeedModelState(refreshing = true)
                repository.save(post)
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }

    }

    fun loadPosts() {

       viewModelScope.launch {

           try {
               _state.value = FeedModelState(loading = true)
               repository.getAll()
               _state.value = FeedModelState()
           } catch (e: Exception) {
               _state.value = FeedModelState(error = true)
           }
       }
    }

    private suspend fun setObserveEditOpenPost(id: Long) {
        if (editedPost.value?.id != 0L && editedPost.value?.id == id) {
                data.collectLatest {posts -> posts.map { post ->
                    if (post.id == editedPost.value?.id) { editedPost.value = post }
                }
            }
        }
        if (openedPost.value?.id != 0L && openedPost.value?.id == id) {
            data.collectLatest {posts  -> posts.map { post ->
                    if (post.id == openedPost.value?.id) { openedPost.value = post }
                }
            }
        }
    }

    override fun onLike(post: Post) {

        viewModelScope.launch {
            try {
                _state.value = FeedModelState(refreshing = true)
                repository.likeById(post.id)
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
                e.printStackTrace()
            }
        }
    }

    override fun onShare(post: Post) {
        //TODO
    }

    override fun onRemove(post: Post) {
        viewModelScope.launch {
            try {
                _state.value = FeedModelState(refreshing = true)
                repository.removeById(post.id)
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    override fun onSaveContent(newContent: String) {
        viewModelScope.launch {
            val text = newContent.trim()
            editedPost.value?.let { thisEditedPost ->
                if (thisEditedPost.content != text) {

                    try {
                        val postForSaved = thisEditedPost.copy(content = text)
                        when (val attachment = media.value) {
                            emptyAttachment -> repository.save(postForSaved)
                            null -> repository.save(postForSaved)
                            else -> repository.saveWithAttachment(postForSaved, attachment?.file)
                        }
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                editedPost.value = emptyPost
                setObserveEditOpenPost(thisEditedPost.id)
            }
        }
    }

    override fun readNeverPosts() {
        viewModelScope.launch {
            repository.readAllPosts()
            _needScrolling.postValue(true)
        }

    }

    override fun onEditPost(post: Post) {
        editedPost.value = post
    }

    override fun onCancelEdit() {
        editedPost.value = emptyPost
    }

    override fun onOpenPost(post: Post) {
        openedPost.value = post
    }

    override fun onOpenAttachment(post: Post) {
        openedAttachment.value = post
    }

    override fun onCancelOpen() {
        openedPost.value = emptyPost
    }

    override fun onCancelOpenAttachment() {
        openedAttachment.value = emptyPost
    }

    fun saveAttachment(uri: Uri?, file: File?) {
        _media.value = MediaModel(uri, file)
    }

    fun clearAttachment() {
        _media.value = emptyAttachment
    }

}
