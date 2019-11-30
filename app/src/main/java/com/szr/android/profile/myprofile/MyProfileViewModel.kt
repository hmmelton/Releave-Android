package com.szr.android.profile.myprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MyProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    enum class ButtonAction {
        EDIT_PROFILE,
        SETTINGS,
        SIGN_OUT
    }

    private val _buttonAction = MutableLiveData<ButtonAction>()

    /**
     * [LiveData] that emits appropriate [ButtonAction] if the user hits a button.
     */
    val buttonAction: LiveData<ButtonAction> = _buttonAction

    fun onEditMyProfileClicked() {
        _buttonAction.value = ButtonAction.EDIT_PROFILE
    }

    fun onSettingsClicked() {
        _buttonAction.value = ButtonAction.SETTINGS
    }

    fun onSignOutClicked() {
        auth.signOut()
        _buttonAction.value = ButtonAction.SIGN_OUT
    }
}