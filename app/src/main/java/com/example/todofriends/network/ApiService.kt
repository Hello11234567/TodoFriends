package com.example.todofriends.network

import com.example.todofriends.model.AuthResponse
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/kakao")
    suspend fun kakaoLogin(
        @Header("Authorization") accessToken: String
    ): AuthResponse
}