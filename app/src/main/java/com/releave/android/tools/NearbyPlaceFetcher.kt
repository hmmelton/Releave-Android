package com.releave.android.tools

import com.google.android.libraries.places.api.model.Place
import io.reactivex.Maybe

interface NearbyPlaceFetcher {

    fun fetchNearbyPlacesList(): Maybe<List<Place>>
}