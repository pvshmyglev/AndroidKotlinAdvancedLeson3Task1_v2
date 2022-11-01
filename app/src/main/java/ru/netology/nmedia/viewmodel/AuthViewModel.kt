package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.LoginUser
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AuthViewModel @Inject constructor (
    private val repository: PostRepository,
    private val auth: AppAuth,
) : ViewModel() {

    val data = auth
        .data
        .asLiveData(Dispatchers.Default)

    val authorized: Boolean
        get() = data.value?.token != null

    private val _authCompleted = SingleLiveEvent<Boolean>()
    val authCompleted: LiveData<Boolean>
        get() = _authCompleted


    fun loginAsUser(login:String, password: String) {
        viewModelScope.launch(Dispatchers.Default) {

            try {
                val authState = repository.loginAsUser(LoginUser(login = login, password = password))
                if (authState.token != null) {
                    auth.setAuth(authState)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            _authCompleted.postValue(true)
        }
    }
}