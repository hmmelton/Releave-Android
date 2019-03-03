package com.hmmelton.releave.models

data class AuthRequestBody(
    val facebookId: String,
    val firstName: String,
    val lastName: String,
    val email: String
)
