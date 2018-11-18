package com.hmmelton.releave.models

import org.threeten.bp.Instant

data class Restroom(
    val name: String,
    val location: String,
    val lat: Double,
    val lng: Double,
    val isLocked: Boolean,
    private var rating: Double,
    private var numRatings: Int,
    val createdBy: String,
    val createdWhen: Instant,
    var updatedBy: String?,
    var updatedWhen: Instant?
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
