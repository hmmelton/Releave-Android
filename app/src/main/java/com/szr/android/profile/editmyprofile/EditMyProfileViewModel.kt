package com.szr.android.profile.editmyprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szr.android.stores.UserInfoStore
import javax.inject.Inject

class EditMyProfileViewModel @Inject constructor(val userInfoStore: UserInfoStore) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val userInfoStore: UserInfoStore) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditMyProfileViewModel(userInfoStore = userInfoStore) as T
        }
    }
}