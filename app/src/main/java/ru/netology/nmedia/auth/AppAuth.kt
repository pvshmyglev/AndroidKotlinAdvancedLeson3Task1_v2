package ru.netology.nmedia.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nmedia.dto.AuthState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context
    ) {
    private val idKey = "id"
    private val tokenKey = "token"

    private val _data = MutableStateFlow(AuthState())
    val data: StateFlow<AuthState>
        get() = _data.asStateFlow()

    init {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0L)

        if (token != null && prefs.contains(token)) {
            _data.value = AuthState(id = id, token = token)
        }
    }

    fun setAuth(authState: AuthState) {
        _data.value = authState
    }

    fun clearAuth() {
        _data.value = AuthState()
    }

}