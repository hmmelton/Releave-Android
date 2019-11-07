package com.szr.android.dagger

import com.szr.android.App
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

@Component(modules = [
    AppModule::class,
    MainActivityModule::class,
    AndroidSupportInjectionModule::class
])
interface AppComponent {
    fun inject(app: App)
}