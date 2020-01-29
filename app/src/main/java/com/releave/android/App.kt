package com.releave.android

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.mapbox.mapboxsdk.Mapbox
import com.releave.android.dagger.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App : Application(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector() = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        initializeDagger()
        Mapbox.getInstance(this, getString(R.string.mapbox_access_key))
        Places.initialize(this, getString(R.string.places_api_key))
    }

    private fun initializeDagger() {
        DaggerAppComponent.factory()
            .create(this)
            .inject(this)
    }
}