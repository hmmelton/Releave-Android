package com.releave.android.data

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.releave.android.data.models.UserInfo
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class is used to keep track of additional user info, beyond the basic name, email, etc
 * provided by Firebase. The saving of additional user info should be considered carefully, to
 * prevent issues with personal data.
 *
 * @param preferences SharedPreferences file used to store user data locally
 * @param auth instance of [FirebaseAuth] used to fetch user ID
 */
@Singleton
class UserSession @Inject constructor(
    private val preferences: SharedPreferences,
    private val auth: FirebaseAuth,
    rootDatabaseReference: FirebaseFirestore
) {

    companion object {
        private const val USER_TABLE_REFERENCE = "user_info"

        private const val KEY_SCREEN_NAME = "com.template.android.screen_name"
        private const val KEY_AGE = "com.template.android.age"
        private const val KEY_BIO = "com.template.android.bio"
    }

    private val collection = rootDatabaseReference.collection(USER_TABLE_REFERENCE)
    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    @SuppressLint("ApplySharedPref")
    fun syncUserInfo() = Maybe.create<UserInfo> { emitter ->
        if (userId.isEmpty()) emitter.onError(IllegalArgumentException("User ID cannot be empty"))

        // This listener will fire when it is first connected
        collection.document(userId).get().addOnSuccessListener { snapshot ->
            snapshot.data?.let { map ->
                val info = UserInfo(map)
                preferences.edit()
                    .putString(KEY_SCREEN_NAME, info.screenName)
                    .putInt(KEY_AGE, info.age)
                    .putString(KEY_BIO, info.bio)
                    .commit()

                emitter.onSuccess(info)
            } ?: run {
                emitter.onComplete()
            }
        }.addOnFailureListener { ex ->
            emitter.onError(ex)
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setUserInfo(value: UserInfo) =  Single.create<Boolean> { emitter ->
        collection.document(userId).set(value.toMap()).addOnSuccessListener {
            preferences.edit()
                .putString(KEY_SCREEN_NAME, value.screenName)
                .putInt(KEY_AGE, value.age)
                .putString(KEY_BIO, value.bio)
                .commit()

            emitter.onSuccess(true)
        }.addOnFailureListener {
            emitter.onSuccess(false)
        }
    }

    @SuppressLint("ApplySharedPref")
    fun deleteAccount() = Single.create<Boolean> { emitter ->
        collection.document(userId).delete().addOnSuccessListener {
            preferences.edit()
                .remove(KEY_SCREEN_NAME)
                .remove(KEY_AGE)
                .remove(KEY_BIO)
                .commit()

            emitter.onSuccess(true)
        }.addOnFailureListener {
            emitter.onSuccess(false)
        }
    }

    fun getUserInfo(): UserInfo {
        val screenName = preferences.getString(KEY_SCREEN_NAME, "") ?: ""
        val age = preferences.getInt(KEY_AGE, 0)
        val bio = preferences.getString(KEY_BIO, "") ?: ""

        return UserInfo(
            screenName = screenName,
            age = age,
            bio = bio
        )
    }
}