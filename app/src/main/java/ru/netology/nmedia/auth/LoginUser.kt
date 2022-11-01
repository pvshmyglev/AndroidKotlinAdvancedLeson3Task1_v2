package ru.netology.nmedia.auth

data class LoginUser(
    val name: String = "",
    val login: String = "",
    val password: String = "",
)
