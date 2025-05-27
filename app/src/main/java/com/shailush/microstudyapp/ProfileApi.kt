package com.shailush.microstudyapp

import com.shailush.microstudyapp.models.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

// data/api/ProfileApi.kt
interface ProfileApi {
    @GET("api/profile/{userId}")  // Используем GET и путь с параметром
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long  // Параметр пути
    ): Response<ProfileResponse>
}