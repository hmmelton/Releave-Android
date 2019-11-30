package com.szr.android.dagger

import com.szr.android.profile.editmyprofile.EditMyProfileFragment
import com.szr.android.profile.editmyprofile.EditMyProfileViewModel
import com.szr.android.data.UserSession
import com.szr.android.signin.SignInFragment
import com.szr.android.signin.SignInViewModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector(modules = [EditMyProfileModule::class])
    abstract fun provideEditMyProfileFragment(): EditMyProfileFragment

    @ContributesAndroidInjector(modules = [SignInModule::class])
    abstract fun provideSignInFragment(): SignInFragment

    @Module
    class EditMyProfileModule {

        @Provides
        fun provideEditMyProfileViewModelFactory(
            userSession: UserSession
        ): EditMyProfileViewModel.Factory {
            return EditMyProfileViewModel.Factory(userSession)
        }
    }

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