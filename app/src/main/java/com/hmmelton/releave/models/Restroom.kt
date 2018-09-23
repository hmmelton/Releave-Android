package com.hmmelton.releave.models

import org.threeten.bp.Instant

data class Restroom(
    val name: String,
    val streetAddress1: String,
    val streetAddress2: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val coords: GeoPoint,
    val isLocked: Boolean,
    private var rating: Double,
    private var numRatings: Int,
    private val createdBy: String,
    private val createdWhen: Instant,
    private var updatedBy: String,
    private var updatedWhen: Instant
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
