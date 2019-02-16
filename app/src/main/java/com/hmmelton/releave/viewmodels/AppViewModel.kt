package com.hmmelton.releave.viewmodels

import android.content.SharedPreferences
import com.hmmelton.releave.adapters.InstantTypeAdapter
import com.hmmelton.releave.models.User
import com.hmmelton.releave.services.ReleaveService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit

class AppViewModel(
    private val preferences: SharedPreferences,
    private val serverAddress: String
) {

    companion object {
        private const val PREFS_KEY_USER = "user"
    }

    private val moshi = Moshi.Builder()
        .add(InstantTypeAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter(User::class.java)

    var currentUser: User?
        get() {
            val userJson = preferences.getString(PREFS_KEY_USER, "").trim()
            return if (userJson.isEmpty()) null else moshi.fromJson(userJson)
        }
        set(value) {
            preferences.edit().putString(PREFS_KEY_USER, moshi.toJson(value)).apply()
        }

    val service: ReleaveService by lazy {
        Retrofit.Builder()
            .baseUrl(serverAddress)
            .build()
            .create(ReleaveService::class.java)
    }
}
