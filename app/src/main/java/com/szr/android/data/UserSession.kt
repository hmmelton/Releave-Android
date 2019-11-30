package com.szr.android.data

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.szr.android.data.models.UserInfo
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
 * @param userId ID of current user
 */
@Singleton
class UserSession @Inject constructor(
    private val preferences: SharedPreferences,
    private val auth: FirebaseAuth,
    rootDatabaseReference: FirebaseFirestore
) {

    companion object {
        private const val USER_TABLE_REFERENCE = "user_info"

        private const val KEY_SCREEN_NAME = "com.szr.android.screen_name"
        private const val KEY_AGE = "com.szr.android.age"
        private const val KEY_BIO = "com.szr.android.bio"
        private const val KEY_IMAGE_RES = "com.szr.android.image_res"
        private const val KEY_BLOCKED_USER_IDS = "com.szr.android.blocked_user_ids"
    }

    private val collection = rootDatabaseReference.collection(USER_TABLE_REFERENCE)
    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    private val gson = GsonBuilder().create()

    fun syncUserInfo() = Maybe.create<UserInfo> { emitter ->
        if (userId.isEmpty()) emitter.onError(IllegalArgumentException("User ID cannot be empty"))

        // This listener will fire when it is first connected
        collection.document(userId).get().addOnSuccessListener { snapshot ->
            snapshot.data?.let {
                emitter.onSuccess(UserInfo(it))
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
                .putString(KEY_IMAGE_RES, value.imageRes)
                .putStringSet(KEY_BLOCKED_USER_IDS, value.blockedUserIds)
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
                .remove(KEY_IMAGE_RES)
                .remove(KEY_BLOCKED_USER_IDS)
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
        val imageRes = preferences.getString(KEY_IMAGE_RES, "") ?: ""
        val blockedUserIds = preferences.getStringSet(KEY_BLOCKED_USER_IDS, null)
            ?: emptySet()

        return UserInfo(
            screenName = screenName,
            age = age,
            bio = bio,
            imageRes = imageRes,
            blockedUserIds = blockedUserIds
        )
    }
}