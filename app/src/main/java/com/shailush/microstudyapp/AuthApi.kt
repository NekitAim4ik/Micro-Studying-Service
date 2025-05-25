package com.shailush.microstudyapp

import com.shailush.microstudyapp.models.AuthResponse
import com.shailush.microstudyapp.models.LoginRequest
import com.shailush.microstudyapp.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}