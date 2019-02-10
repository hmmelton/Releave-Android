package com.hmmelton.releave

import android.app.Application
import android.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.hmmelton.releave.models.User
import com.hmmelton.releave.services.ReleaveService
import com.hmmelton.releave.viewmodels.AppViewModel
import io.fabric.sdk.android.Fabric
import retrofit2.Retrofit

class App : Application() {

    private lateinit var viewModel: AppViewModel

    lateinit var releaveService: ReleaveService
        private set

    var currentUser: User?
        get() = viewModel.currentUser
        set(value) {
            viewModel.currentUser = value
        }

    override fun onCreate() {
        super.onCreate()

        viewModel = AppViewModel(PreferenceManager.getDefaultSharedPreferences(this))

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
