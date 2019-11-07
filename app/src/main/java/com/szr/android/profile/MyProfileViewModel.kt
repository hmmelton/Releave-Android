package com.szr.android.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MyProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _shouldSignOut = MutableLiveData<Boolean>()

    /**
     * [LiveData] that emits true [Boolean] if the user hits the sign out button.
     */
    val shouldSignOut: LiveData<Boolean> = _shouldSignOut

    fun onEditMyProfileClicked() {
    }

    fun onSettingsClicked() {
    }

    fun onSignOutClicked() {
        auth.signOut()
        _shouldSignOut.postValue(true)
    }
}