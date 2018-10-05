package com.hmmelton.releave.data.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.Instant

@Entity
data class Restroom(

    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "street_address_1")
    val streetAddress1: String,

    @ColumnInfo(name = "street_address_2")
    val streetAddress2: String,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "postal_code")
    val postalCode: String,

    @ColumnInfo(name = "latitude")
    val lat: Double,

    @ColumnInfo(name = "longitude")
    val lng: Double,

    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean,

    @ColumnInfo(name = "rating")
    private var rating: Double,

    @ColumnInfo(name = "num_ratings")
    private var numRatings: Int,

    @ColumnInfo(name = "created_by")
    val createdBy: String,

    @ColumnInfo(name = "created_when")
    val createdWhen: Instant,

    @ColumnInfo(name = "updated_by")
    var updatedBy: String,

    @ColumnInfo(name = "updated_when")
    var updatedWhen: Instant
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
