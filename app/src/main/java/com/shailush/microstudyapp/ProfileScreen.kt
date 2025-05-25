package com.shailush.microstudyapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(onBackClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
                Text(
                    text = "Профиль",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            UserInfoSection()
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            CompletedCoursesSection()
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            AchievementsSection()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UserInfoSection() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Аноним Аноним", style = MaterialTheme.typography.titleLarge)
            Text("ID: 1", color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Дата регистрации: 26.05.2025")
        }
    }
}

@Composable
private fun CompletedCoursesSection() {
    Column {
        Text("Пройденные курсы", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {}) {
                    Text("Перейти")
                }
            }
        }
    }
}

@Composable
private fun AchievementsSection() {
    Column {
        Text("Достижения", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("• 0 дней подряд")
                Text("• 0 баллов")
            }
        }
    }
}
