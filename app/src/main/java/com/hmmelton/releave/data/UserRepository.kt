package com.hmmelton.releave.data

import android.content.SharedPreferences
import com.hmmelton.releave.adapters.InstantTypeAdapter
import com.hmmelton.releave.data.models.User
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object UserRepository {

    private const val USER_KEY = "user"

    private val moshi = Moshi.Builder()
        .add(InstantTypeAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter(User::class.java)

    fun setCurrentUser(user: User, preferences: SharedPreferences) {
        preferences.edit().putString(USER_KEY, moshi.toJson(user)).apply()
    }

    fun getCurrentUser(preferences: SharedPreferences): User? {
        val userJson = preferences.getString(USER_KEY, "").trim()
        return if (userJson.isEmpty()) null else moshi.fromJson(userJson)
    }
}
