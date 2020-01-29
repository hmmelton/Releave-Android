package com.releave.android.dagger

import android.content.Context
import com.releave.android.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    AppModule.ProvidesModule::class,
    MainActivityModule::class,
    FragmentsModule::class,
    AndroidSupportInjectionModule::class
])
@Singleton
interface AppComponent {

    fun inject(app: App)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance @AppContext context: Context): AppComponent
    }
}