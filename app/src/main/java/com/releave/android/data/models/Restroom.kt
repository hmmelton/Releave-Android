package com.releave.android.data.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

class Restroom(
    val name: String,
    val streetAddress: String,
    val location: LatLng,
    val avgRating: Double,
    val numRatings: Int,
    val isLocked: Boolean,
    val isSingleOccupancy: Boolean,
    val isGenderNeutral: Boolean,
    val createdBy: String,
    val createdWhen: Timestamp
)