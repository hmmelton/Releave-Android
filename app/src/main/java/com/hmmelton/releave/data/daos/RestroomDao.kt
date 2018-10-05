package com.hmmelton.releave.data.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.hmmelton.releave.data.models.Restroom

private const val QUERY_STRING_VISIBLE_RESTROOMS = """
        SELECT * FROM restroom
        WHERE latitude >= :startLat
        AND latitude <= :endLat
        AND longitude >= :startLng
        AND longitude <= :endLng
    """

@Dao
interface RestroomDao {
    @Query("SELECT * from restroom")
    fun getAll(): List<Restroom>

    @Query(QUERY_STRING_VISIBLE_RESTROOMS)
    fun getVisibleRestrooms(startLat: Double, endLat: Double, startLng: Double, endLng: Double): List<Restroom>

    @Query("SELECT * FROM restroom WHERE created_by = :userId")
    fun getByUserId(userId: String): Restroom?

    @Insert
    fun insert(restroom: Restroom)

    @Delete
    fun delete(restroom: Restroom)
}
