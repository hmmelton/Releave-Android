package com.szr.android.profile.editmyprofile

import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.szr.android.models.NotifiableObservable
import com.szr.android.models.NotifiableObservableImpl
import com.szr.android.models.UserInfo
import com.szr.android.stores.UserInfoStore
import io.reactivex.disposables.Disposable
import javax.inject.Inject

private const val TAG = "EditMyProfileVM"
class EditMyProfileViewModel @Inject constructor(
    val userInfoStore: UserInfoStore,
    val auth: FirebaseAuth,
    observable: NotifiableObservable = NotifiableObservableImpl()
) : ViewModel(), NotifiableObservable by observable {

    companion object {
        private const val MINIMUM_AGE = 18
    }

    var screenName: String = ""
        @Bindable get
        set(value) {
            field = value
        }

    var age: String = ""
        @Bindable get
        set(value) {
            field = value
        }

    var bio: String = ""
        @Bindable get
        set(value) {
            field = value
        }

    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    private var userInfo: UserInfo = UserInfo()

    private val _action = MutableLiveData<Action>()
    val action: LiveData<Action> = _action

    private val userInfoDisposable : Disposable = userInfoStore.get(userId = userId).subscribe(
        { info ->
            _action.postValue(Action.HIDE_SPINNER)
            userInfo = info
        },
        { err ->
            _action.postValue(Action.HIDE_SPINNER)
            Log.e(TAG, err.message ?: "Unknown error")
        },
        {
            _action.postValue(Action.HIDE_SPINNER)
            Log.i(TAG, "Completed without data")
        }
    )

    init {
        _action.postValue(Action.SHOW_SPINNER)
    }

    override fun onCleared() {
        super.onCleared()
        userInfoDisposable.dispose()
    }

    fun saveData() {
        if (age.toInt() < MINIMUM_AGE) {
            _action.postValue(Action.ERROR_AGE_TOO_LOW)
            return
        }

        userInfo.apply {
            screenName = this@EditMyProfileViewModel.screenName
            age = this@EditMyProfileViewModel.age.toInt()
            bio = this@EditMyProfileViewModel.bio
        }
        userInfoStore.set(userInfo, userId)
    }

    enum class Action {
        HIDE_SPINNER,
        SHOW_SPINNER,
        ERROR_AGE_TOO_LOW
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val userInfoStore: UserInfoStore,
        private val auth: FirebaseAuth
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditMyProfileViewModel(
                userInfoStore = userInfoStore,
                auth = auth
            ) as T
        }
    }
}