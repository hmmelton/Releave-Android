package com.szr.android

import android.app.Application
import com.szr.android.dagger.DaggerAppComponent
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
    }

    private fun createDaggerInjections() {
        DaggerAppComponent.create().inject(this)
    }
}