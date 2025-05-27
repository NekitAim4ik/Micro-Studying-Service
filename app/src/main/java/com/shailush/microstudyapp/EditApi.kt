package com.shailush.microstudyapp

import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.Response
import retrofit2.http.Header

interface EditApi {
    @PUT("/api/profile/updateProfile")
    suspend fun updateProfile(@Header("Authorization") token: String, @Body profile: ProfileUpdateRequest): Response<Unit>
}

data class ProfileUpdateRequest(
    val id: Long,
    val name: String,
    val surname: String
)