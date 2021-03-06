package com.releave.android.dagger

import com.releave.android.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [MainActivitySubcomponent::class])
abstract class MainActivityModule {
    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    abstract fun bindMainActivityInjectorFactory(
        factory: MainActivitySubcomponent.Factory
    ): AndroidInjector.Factory<*>
}