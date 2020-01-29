package com.releave.android.dagger

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.releave.android.tools.NearbyPlaceFetcher
import com.releave.android.tools.NearbyPlacesFetcherImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Module
    internal object ProvidesModule {
        @Provides
        @Singleton
        fun provideSharedPreferences(
            @AppContext context: Context
        ) = PreferenceManager.getDefaultSharedPreferences(context)

        @Provides
        @Singleton
        fun provideFirestoreReference() = FirebaseFirestore.getInstance()

        @Provides
        @Singleton
        fun provideFirebaseAuth() = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun providePlacesClient(@AppContext context: Context): PlacesClient = Places.createClient(context)
    }

    @Binds
    abstract fun provideNearbyPlacesFetcher(
        nearbyPlacesFetcherImpl: NearbyPlacesFetcherImpl
    ): NearbyPlaceFetcher
}
