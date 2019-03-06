package com.hmmelton.releave.data.models

import com.squareup.moshi.Json
import org.threeten.bp.Instant

data class Restroom(
    @Json(name = "_id") val id: String,
    val name: String,
    val location: String,
    val lat: Double,
    val lng: Double,
    @Json(name = "is_locked") val isLocked: Boolean,
    @Json(name = "is_single_occupancy") val isSingleOccupancy: Boolean,
    private var rating: Double,
    @Json(name = "num_ratings") private var numRatings: Int,
    @Json(name = "created_by") val createdBy: String,
    @Json(name = "created_when") val createdWhen: Instant,
    @Json(name = "updated_by") var updatedBy: String?,
    @Json(name = "updated_when") var updatedWhen: Instant?
) {

    /**
     * Add a rating to this Restroom object.
     *
     * @param updatedBy id of user who is adding this rating
     * @param rating rating to be added to this Restroom
     */
    fun addRating(updatedBy: String, rating: Int) {
        val cumulativeRating = this.rating * this.numRatings

        // Update rating and number of ratings
        this.numRatings = this.numRatings + 1
        this.rating = (cumulativeRating + rating) / this.numRatings

        // Record update time and user
        this.updatedBy = updatedBy
        this.updatedWhen = Instant.now()
    }

    fun getNumRatings() = numRatings

    fun getRating() = rating
}
