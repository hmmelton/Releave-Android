package com.hmmelton.releave.data.models

import com.squareup.moshi.Json
import org.threeten.bp.Instant

data class User(
    @Json(name = "_id") val id: String,
    @Json(name = "facebook_id") val facebookId: String,
    @Json(name = "created_when") val createdWhen: Instant,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    val email: String,
    val paid: Boolean = false
)
