// Сервис для работы с аутентификацией
package com.shailush.microstudyapp.data.service

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.auth0.android.jwt.JWT
import com.shailush.microstudyapp.AuthApi
import com.shailush.microstudyapp.EditApi
import com.shailush.microstudyapp.ProfileApi
import com.shailush.microstudyapp.ProfileUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import com.shailush.microstudyapp.models.LoginRequest
import com.shailush.microstudyapp.models.ProfileResponse
import com.shailush.microstudyapp.models.RegisterRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response

class AuthService(private val context: Context) {
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(AuthApi::class.java)
    private val profileApi = retrofit.create(ProfileApi::class.java)
    private val apiService = retrofit.create(EditApi::class.java)

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    suspend fun loadProfile(): ProfileResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val token = getToken()
                val userId = getUserId()

                if (token != null && userId != null) {
                    val response = profileApi.getProfile(
                        "Bearer $token",
                        userId  // Теперь передаем только ID как часть пути
                    )

                    if (response.isSuccessful) {
                        response.body()
                    } else {
                        Log.e("ProfileLoad", "Server error: ${response.code()}")
                        null
                    }
                } else {
                    Log.e("ProfileLoad", "Token or userId is null")
                    null
                }
            } catch (e: Exception) {
                Log.e("ProfileLoad", "Network error", e)
                null
            }
        }
    }

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "auth_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun saveAuthData(userId: Long, token: String) {
        sharedPreferences.edit().apply {
            putLong("user_id", userId)
            putString("jwt_token", token)
            apply()
        }
    }

    fun getUserId(): Long? {
        return if (sharedPreferences.contains("user_id")) {
            sharedPreferences.getLong("user_id", -1).takeIf { it != -1L }
        } else {
            null
        }
    }

    suspend fun updateProfile(profile: ProfileUpdateRequest): Boolean {
        return try {
            val token = getToken()
            val response = apiService.updateProfile("Bearer $token", profile)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }


    suspend fun login(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        saveAuthData(authResponse.id, authResponse.token)
                        true
                    } ?: false
                } else {
                    false
                }
            } catch (e: IOException) {
                Log.e("AuthService", "Register error", e)
                false
            } catch (e: Exception) {
                Log.e("AuthService", "Unexpected error", e)
                false
            }
        }
    }

    suspend fun register(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.register(RegisterRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        saveAuthData(authResponse.id, authResponse.token)
                        true
                    } ?: false
                } else {
                    false
                }
            } catch (e: IOException) {
                Log.e("AuthService", "Register error", e)
                false
            } catch (e: Exception) {
                Log.e("AuthService", "Unexpected error", e)
                false
            }
        }
    }

    fun getToken(): String? = sharedPreferences.getString("jwt_token", null)

    fun isLoggedIn(): Boolean {
        val token = getToken()
        return if (token != null) {
            try {
                val jwt = JWT(token)
                !jwt.isExpired(3600)
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }

    fun logout() {
        sharedPreferences.edit().apply {
            remove("jwt_token")
            remove("user_id")
            apply()
        }
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit().putString("jwt_token", token).apply()
    }

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.Main) {
            val result = login(email, password)
            callback(result)
        }
    }

    fun register(email: String, password: String, callback: (Boolean) -> Unit) {
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.Main) {
            val result = register(email, password)
            callback(result)
        }
    }
}