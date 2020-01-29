package com.releave.android.dagger

import com.releave.android.fragments.main.MainFragment
import com.releave.android.fragments.main.MainViewModel
import com.releave.android.tools.NearbyPlaceFetcher
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun providesMainFragment(): MainFragment

    @Module
    class MainModule {

        @Provides
        fun providesMainViewModelFactory(
            nearbyPlaceFetcher: NearbyPlaceFetcher
        ): MainViewModel.Factory {
            return MainViewModel.Factory(nearbyPlaceFetcher)
        }
    }
}