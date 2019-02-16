package com.hmmelton.releave

import android.app.Application
import android.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.hmmelton.releave.models.User
import com.hmmelton.releave.services.ReleaveService
import com.hmmelton.releave.viewmodels.AppViewModel
import io.fabric.sdk.android.Fabric

class App : Application() {

    private lateinit var viewModel: AppViewModel

    val releaveService: ReleaveService by lazy {
        viewModel.service
    }

    var currentUser: User?
        get() = viewModel.currentUser
        set(value) {
            viewModel.currentUser = value
        }

    override fun onCreate() {
        super.onCreate()

        viewModel = AppViewModel(
            preferences = PreferenceManager.getDefaultSharedPreferences(this),
            serverAddress = Secrets.SERVER_ADDRESS
        )

        initFabric()
    }

    private fun initFabric() {
        val fabric = Fabric.Builder(this)
            .kits(Crashlytics())
            .debuggable(true)
            .build()
        Fabric.with(fabric)
    }
}
