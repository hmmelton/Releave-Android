package com.szr.android.dagger

import com.szr.android.App
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [
    AppModule::class,
    MainActivityModule::class,
    FragmentsModule::class,
    AndroidSupportInjectionModule::class
])
@Singleton
interface AppComponent {
    fun inject(app: App)
}