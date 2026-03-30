package com.example.todofriends.network

import com.example.todofriends.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("auth/kakao")
    suspend fun kakaoLogin(
        @Header("Authorization") accessToken: String
    ): AuthResponse

    @PUT("api/users/me/app-id")
    suspend fun updateAppId(
        @Header("Authorization") token: String,
        @Body body: AppIdRequest
    ): Response<String>
}