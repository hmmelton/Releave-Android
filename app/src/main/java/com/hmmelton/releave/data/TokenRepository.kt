package com.hmmelton.releave.data

import android.content.SharedPreferences

object TokenRepository {

    private const val TOKEN_KEY = "access_token"

    fun setToken(token: String?, preferences: SharedPreferences) {
        synchronized(this) {
            preferences.edit().putString(TOKEN_KEY, token).apply()
        }
    }

    fun getToken(preferences: SharedPreferences): String? {
        synchronized(this) {
            return preferences.getString(TOKEN_KEY, null)
        }
    }
}
