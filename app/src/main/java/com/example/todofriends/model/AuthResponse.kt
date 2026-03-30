package com.example.todofriends.model

data class AuthResponse(
    val token: String,
    val nickname: String,
    val isRegistered: Boolean
)