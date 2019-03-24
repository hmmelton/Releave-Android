package com.hmmelton.releave.data.models

import com.squareup.moshi.Json

data class RestroomRequestBody(
    @Json(name = "created_by") val createdBy: String,
    val lat: Double,
    val lng: Double,
    val name: CharSequence,
    val location: CharSequence,
    @Json(name = "is_locked") val isLocked: Boolean = true,
    @Json(name = "is_single_occupancy") val isSingleOccupancy: Boolean = false,
    val rating: Double
)
