package com.releave.android.fragments.main

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModel
import com.releave.android.data.models.NotifiableObservable
import com.releave.android.data.models.NotifiableObservableImpl

class MainViewModel(
    observable: NotifiableObservable = NotifiableObservableImpl()
) : ViewModel(), NotifiableObservable by observable {

    var areLocationPermissionsGranted: Boolean = true
        @Bindable get
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.areLocationPermissionsGranted)
            }
        }

    init {
        (observable as? NotifiableObservableImpl)?.sender = this
    }
}