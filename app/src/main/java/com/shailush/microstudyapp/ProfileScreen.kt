package com.shailush.microstudyapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shailush.microstudyapp.data.service.AuthService
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import com.shailush.microstudyapp.models.ProfileResponse
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TextField
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    authService: AuthService
) {
    // Используем rememberCoroutineScope для создания корутин в композейбл-функциях
    val scope = rememberCoroutineScope()

    var profile by remember { mutableStateOf<ProfileResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var tempSurname by remember { mutableStateOf("") }

    // Загрузка профиля при первом отображении
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            profile = authService.loadProfile()
        } catch (e: Exception) {
            error = "Ошибка загрузки: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Шапка с кнопками
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
            Text("Профиль", style = MaterialTheme.typography.headlineMedium)
            IconButton(
                onClick = {
                    profile?.let {
                        tempName = it.name
                        tempSurname = it.surname
                        isEditing = true
                    }
                },
                enabled = !isEditing && profile != null
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = if (isEditing || profile == null) Color.LightGray
                    else MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            error != null -> Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            profile != null && !isEditing -> ProfileInfo(profile!!)
            isEditing -> EditProfileForm(
                initialName = tempName,
                initialSurname = tempSurname,
                onNameChange = { tempName = it },
                onSurnameChange = { tempSurname = it },
                onSave = {
                    scope.launch {  // Правильный вызов корутины
                        isLoading = true
                        try {
                            val updateRequest = ProfileUpdateRequest(
                                id = profile!!.id,
                                name = tempName,
                                surname = tempSurname
                            )
                            val success = authService.updateProfile(updateRequest)
                            if (success) {
                                profile = profile!!.copy(
                                    name = tempName,
                                    surname = tempSurname
                                )
                                isEditing = false
                            } else {
                                error = "Ошибка сохранения профиля"
                            }
                        } catch (e: Exception) {
                            error = "Ошибка сети: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                onCancel = {
                    isEditing = false
                }
            )
        }
    }
}

private suspend fun loadProfile(
    authService: AuthService,
    onSuccess: (ProfileResponse) -> Unit,
    onError: (String) -> Unit,
    onLoading: (Boolean) -> Unit
) {
    onLoading(true)
    try {
        val profile = authService.loadProfile()
        if (profile != null) {
            onSuccess(profile)
        } else {
            onError("Не удалось загрузить профиль")
        }
    } catch (e: Exception) {
        onError("Ошибка загрузки: ${e.message}")
    } finally {
        onLoading(false)
    }
}

@Composable
fun ProfileInfo(profile: ProfileResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${profile.name} ${profile.surname}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("ID: ${profile.id}")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Дата регистрации: ${profile.registrationDate}")
        }
    }
}

@Composable
fun EditProfileForm(
    initialName: String,
    initialSurname: String,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Редактирование профиля", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = initialName,
                onValueChange = onNameChange,
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = initialSurname,
                onValueChange = onSurnameChange,
                label = { Text("Фамилия") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "Отмена")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Отмена")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSave) {
                    Icon(Icons.Default.Check, contentDescription = "Сохранить")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Сохранить")
                }
            }
        }
    }
}
