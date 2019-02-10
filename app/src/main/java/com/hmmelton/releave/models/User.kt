package com.hmmelton.releave.models

import com.squareup.moshi.Json
import org.threeten.bp.Instant

data class User(
    val id: String,
    @Json(name = "created_when") val createdWhen: Instant = Instant.now(),
    @Json(name = "facebook_id") val facebookId: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    val email: String,
    @Json(name = "auth_token") val authToken: String,
    val paid: Boolean = false
)
