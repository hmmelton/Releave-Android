package com.hmmelton.releave.data.models

import com.squareup.moshi.Json

data class AuthRequestBody(
    @Json(name = "facebook_id") val facebookId: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    val email: String
)
