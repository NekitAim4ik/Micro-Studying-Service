package com.shailush.microstudyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun TaskScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var answerChecked by remember { mutableStateOf(false) }

    val question = "Выберите цифру 1"
    val correctAnswerIndex = 0 // Правильный ответ (val)
    val options = listOf("1", "2", "3", "4")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Шапка с кнопкой назад
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Выбранная тема
        Text(
            text = "Основы Kotlin",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Задание
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Варианты ответов
        Text(
            text = "Варианты ответов",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val isCorrect = index == correctAnswerIndex

                val backgroundColor = when {
                    !answerChecked && isSelected -> MaterialTheme.colorScheme.primaryContainer
                    answerChecked && isCorrect -> Color(0xFFE8F5E9) // Светло-зеленый
                    answerChecked && isSelected && !isCorrect -> Color(0xFFFFEBEE) // Светло-красный
                    else -> MaterialTheme.colorScheme.surface
                }

                val borderColor = when {
                    answerChecked && isCorrect -> Color(0xFF4CAF50) // Зеленый
                    answerChecked && isSelected && !isCorrect -> Color(0xFFF44336) // Красный
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isSelected,
                            onClick = {
                                if (!answerChecked) {
                                    selectedOption = index
                                }
                            },
                            role = Role.RadioButton
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor
                    ),
                    border = CardDefaults.outlinedCardBorder(true)
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка продолжить
        Button(
            onClick = {
                if (!answerChecked) {
                    answerChecked = true
                } else {
                    onContinueClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = selectedOption != null
        ) {
            Text(if (!answerChecked) "Проверить" else "Продолжить")
        }
    }
}