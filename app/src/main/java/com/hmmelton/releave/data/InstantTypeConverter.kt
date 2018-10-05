package com.hmmelton.releave.data

import android.arch.persistence.room.TypeConverter
import org.threeten.bp.Instant

class InstantTypeConverter {

    @TypeConverter
    fun toLong(instant: Instant) = instant.toEpochMilli()

    @TypeConverter
    fun toInstant(millis: Long) = Instant.ofEpochMilli(millis)
}
