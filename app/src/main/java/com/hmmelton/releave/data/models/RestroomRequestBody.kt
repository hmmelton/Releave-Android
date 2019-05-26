package com.hmmelton.releave.data.models

import com.squareup.moshi.Json

data class RestroomRequestBody(
    @Json(name = "created_by") val createdBy: String,
    @Json(name = "updated_by") val updatedBy: String = createdBy,
    val lat: Double,
    val lng: Double,
    val name: String,
    val location: String,
    @Json(name = "is_locked") val isLocked: Boolean = true,
    @Json(name = "is_single_occupancy") val isSingleOccupancy: Boolean = false,
    val rating: Int
)
