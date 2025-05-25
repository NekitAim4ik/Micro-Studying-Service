// Сервис для работы с аутентификацией
package com.shailush.microstudyapp.data.service

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.auth0.android.jwt.JWT
import com.shailush.microstudyapp.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import com.shailush.microstudyapp.models.LoginRequest
import com.shailush.microstudyapp.models.RegisterRequest

class AuthService(private val context: Context) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(AuthApi::class.java)

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "auth_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun login(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    token?.let {
                        saveToken(it)
                        true
                    } ?: false
                } else {
                    false
                }
            } catch (e: IOException) {
                Log.e("AuthService", "Login error", e)
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
                    val token = response.body()?.token
                    token?.let {
                        saveToken(it)
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
        sharedPreferences.edit().remove("jwt_token").apply()
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