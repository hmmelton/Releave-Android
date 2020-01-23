package com.releave.android

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import com.releave.android.dagger.AppModule
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
        createDaggerInjections()
        Mapbox.getInstance(this, getString(R.string.mapbox_access_key))
    }

    private fun createDaggerInjections() {
        DaggerAppComponent.builder()
            .appModule(AppModule(context = this))
            .build()
            .inject(this)
    }
}