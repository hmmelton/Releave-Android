package com.hmmelton.releave.viewmodels

import android.content.SharedPreferences
import com.hmmelton.releave.adapters.InstantTypeAdapter
import com.hmmelton.releave.models.User
import com.squareup.moshi.Moshi

class AppViewModel(private val preferences: SharedPreferences) {

    companion object {
        private const val PREFS_KEY_USER = "user"
    }

    private val moshi = Moshi.Builder()
        .add(InstantTypeAdapter())
        .build()
        .adapter(User::class.java)

    var currentUser: User?
        get() = moshi.fromJson(preferences.getString(PREFS_KEY_USER, null))
        set(value) {
            preferences.edit().putString(PREFS_KEY_USER, moshi.toJson(value)).apply()
        }
}
