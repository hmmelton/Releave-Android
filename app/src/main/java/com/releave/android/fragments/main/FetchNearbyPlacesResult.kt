package com.releave.android.fragments.main

import com.google.android.libraries.places.api.model.Place

/**
 * This class is used to signal whether or not nearby places were fetched successfully.
 */
sealed class FetchNearbyPlacesResult {
    class Error(val throwable: Throwable) : FetchNearbyPlacesResult()

    class Success(val places: List<Place>) : FetchNearbyPlacesResult()
}