package com.hmmelton.releave.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.hmmelton.releave.data.daos.RestroomDao
import com.hmmelton.releave.data.models.Restroom

@Database(entities = [(Restroom::class)], version = 1)
@TypeConverters(InstantTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val restroomDao: RestroomDao
}
