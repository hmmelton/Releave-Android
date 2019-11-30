package com.szr.android.profile.editmyprofile

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.szr.android.addTo
import com.szr.android.data.UserSession
import com.szr.android.data.models.NotifiableObservable
import com.szr.android.data.models.NotifiableObservableImpl
import com.szr.android.data.models.UserInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EditMyProfileViewModel @Inject constructor(
    val userSession: UserSession,
    observable: NotifiableObservable = NotifiableObservableImpl()
) : ViewModel(), NotifiableObservable by observable {

    companion object {
        private const val MINIMUM_AGE = 18
    }

    var screenName: String = ""
        @Bindable get
        set(value) {
            field = value.trim()
            notifyPropertyChanged(BR.screenName)
        }

    var age: String = ""
        @Bindable get
        set(value) {
            field = if (value == "0") "" else value.trim()
            notifyPropertyChanged(BR.age)
        }

    var bio: String = ""
        @Bindable get
        set(value) {
            field = value.trim()
            notifyPropertyChanged(BR.bio)
        }

    private var userInfo: UserInfo = userSession.getUserInfo().also { info ->
        screenName = info.screenName
        age = info.age.toString()
        bio = info.bio
    }

    private val _action = MutableLiveData<Action>()
    val action: LiveData<Action> = _action

    private val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun saveData() {
        if (age.trim().isNotEmpty() && age.toInt() < MINIMUM_AGE) {
            _action.value = Action.ERROR_AGE_TOO_LOW
            return
        }

        userInfo.apply {
            screenName = this@EditMyProfileViewModel.screenName
            age = this@EditMyProfileViewModel.age.toInt()
            bio = this@EditMyProfileViewModel.bio
        }
        userSession.setUserInfo(userInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { successful ->
                    if (successful) {
                        _action.value = Action.SAVED
                    } else {
                        _action.value = Action.ERROR_SAVING
                    }
                },
                {
                    _action.value = Action.ERROR_SAVING
                }
            )
            .addTo(disposables)
    }

    enum class Action {
        ERROR_AGE_TOO_LOW,
        ERROR_SAVING,
        SAVED
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val userSession: UserSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditMyProfileViewModel(
                userSession = userSession
            ) as T
        }
    }
}