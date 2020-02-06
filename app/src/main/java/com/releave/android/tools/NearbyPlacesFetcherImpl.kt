package com.releave.android.tools

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import io.reactivex.Maybe
import java.lang.Exception
import javax.inject.Inject

class NearbyPlacesFetcherImpl @Inject constructor(
    private val placesClient: PlacesClient
) : NearbyPlaceFetcher {

    companion object {
        private val PLACE_FIELDS = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
    }

    override fun fetchNearbyPlacesList() = Maybe.create<List<Place>> { emitter ->
        val request = FindCurrentPlaceRequest.newInstance(PLACE_FIELDS)
        placesClient.findCurrentPlace(request).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { response ->
                    emitter.onSuccess(response.placeLikelihoods.map { it.place })
                } ?: run {
                    emitter.onComplete()
                }
            } else {
                emitter.onError(task.exception ?: Exception("Error unknown"))
            }
        }
    }
}