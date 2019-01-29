package com.hmmelton.releave.models

import org.threeten.bp.Instant

data class User(
    val createdWhen: Instant = Instant.now(),
    val facebookId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val authToken: String,
    val paid: Boolean = false
)
