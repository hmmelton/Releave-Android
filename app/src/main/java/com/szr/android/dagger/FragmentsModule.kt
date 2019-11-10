package com.szr.android.dagger

import com.szr.android.profile.editmyprofile.EditMyProfileFragment
import com.szr.android.profile.editmyprofile.EditMyProfileViewModel
import com.szr.android.stores.UserInfoStore
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {

    @ContributesAndroidInjector(modules = [EditMyProfileModule::class])
    abstract fun provideEditMyProfileFragment(): EditMyProfileFragment

    @Module
    class EditMyProfileModule {

        @Provides
        fun provideEditMyProfileViewModelFactory(userInfostore: UserInfoStore) =
            EditMyProfileViewModel.Factory(userInfoStore = userInfostore)
    }
}