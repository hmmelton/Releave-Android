package com.hmmelton.releave

import android.app.Application
import android.arch.persistence.room.Room
import com.crashlytics.android.Crashlytics
import com.hmmelton.releave.data.AppDatabase
import io.fabric.sdk.android.Fabric

class App : Application() {

    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        initFabric()
        initDatabase()
    }

    private fun initFabric() {
        val fabric = Fabric.Builder(this)
            .kits(Crashlytics())
            .debuggable(true)
            .build()
        Fabric.with(fabric)
    }

    private fun initDatabase() {
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "releave-database").build()
    }
}
