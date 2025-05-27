package com.shailush.microstudyapp.models

import java.time.LocalDate

data class ProfileResponse(
    val id: Long,
    val name: String,
    val surname: String,
    val registrationDate: String // или LocalDateTime, в зависимости от формата
)