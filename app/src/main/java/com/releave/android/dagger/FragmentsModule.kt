package com.releave.android.dagger

import com.releave.android.data.UserSession
import com.releave.android.signin.SignInFragment
import com.releave.android.signin.SignInViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector(modules = [SignInModule::class])
    abstract fun provideSignInFragment(): SignInFragment

    @Module
    class SignInModule {

        @Provides
        fun provideSignInViewModelFactory(
            userSession: UserSession
        ): SignInViewModel.Factory {
            return SignInViewModel.Factory(userSession)
        }
    }
}