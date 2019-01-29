package com.hmmelton.releave

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.hmmelton.releave.services.ReleaveService
import io.fabric.sdk.android.Fabric
import retrofit2.Retrofit

class App : Application() {

    lateinit var releaveService: ReleaveService
        private set

    override fun onCreate() {
        super.onCreate()

        initFabric()
        initReleaveService()
    }

    private fun initFabric() {
        val fabric = Fabric.Builder(this)
            .kits(Crashlytics())
            .debuggable(true)
            .build()
        Fabric.with(fabric)
    }

    private fun initReleaveService() {
        releaveService = Retrofit.Builder()
            .baseUrl(getString(R.string.server_address))
            .build()
            .create(ReleaveService::class.java)
    }
}
