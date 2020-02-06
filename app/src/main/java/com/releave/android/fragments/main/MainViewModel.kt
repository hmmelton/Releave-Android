package com.releave.android.fragments.main

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.releave.android.addTo
import com.releave.android.data.NotifiableObservable
import com.releave.android.data.NotifiableObservableImpl
import com.releave.android.tools.NearbyPlaceFetcher
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(
    observable: NotifiableObservable = NotifiableObservableImpl(),
    private val placesFetcher: NearbyPlaceFetcher
) : ViewModel(), NotifiableObservable by observable {

    var areLocationPermissionsGranted: Boolean = true
        @Bindable get
        set(value) {
            if (value != field) {
                field = value
                notifyPropertyChanged(BR.areLocationPermissionsGranted)
            }
        }

    private val _fetchNearbyPlacesResult = MutableLiveData<FetchNearbyPlacesResult>()
    val fetchNearbyPlacesResult = _fetchNearbyPlacesResult

    private val disposables = CompositeDisposable()

    init {
        (observable as? NotifiableObservableImpl)?.sender = this
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    fun fetchNearbyPlaces(view: View) {
        placesFetcher.fetchNearbyPlacesList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ places ->
                // Places successfully fetched
                _fetchNearbyPlacesResult.value = FetchNearbyPlacesResult.Success(places)
            }, { err ->
                // An error occurred while fetching nearby places
                _fetchNearbyPlacesResult.value = FetchNearbyPlacesResult.Error(err)
            }, {
                // No result returned
                // I don't think this will really happen much
                _fetchNearbyPlacesResult.value = FetchNearbyPlacesResult.Success(emptyList())
            })
            .addTo(disposables)
    }

    fun onSubmitRestroom(view: View) {

    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val placesFetcher: NearbyPlaceFetcher) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(placesFetcher = placesFetcher) as T
        }

    }
}