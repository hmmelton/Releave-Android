package com.szr.android.data

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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
    rootDatabaseReference: DatabaseReference
) {

    companion object {
        private const val USER_TABLE_REFERENCE = "user_info"

        private const val KEY_SCREEN_NAME = "com.szr.android.screen_name"
        private const val KEY_AGE = "com.szr.android.age"
        private const val KEY_BIO = "com.szr.android.bio"
        private const val KEY_IMAGE_RES = "com.szr.android.image_res"
        private const val KEY_BLOCKED_USER_IDS = "com.szr.android.blocked_user_ids"
    }

    private val database = rootDatabaseReference.child(USER_TABLE_REFERENCE)
    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    fun syncUserInfo() = Maybe.create<UserInfo> { emitter ->
        if (userId.isEmpty()) emitter.onError(IllegalArgumentException("User ID cannot be empty"))

        // This listener will fire when it is first connected
        database.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(e: DatabaseError) {
                database.removeEventListener(this)
                emitter.onError(e.toException())
            }

            override fun onDataChange(data: DataSnapshot) {
                database.removeEventListener(this)

                val userInfo = data.value as? UserInfo

                if (userInfo == null) {
                    emitter.onComplete()
                } else {
                    emitter.onSuccess(userInfo)
                }
            }
        })
    }

    @SuppressLint("ApplySharedPref")
    fun setUserInfo(value: UserInfo) =  Single.create<Boolean> { emitter ->
        database.child(userId).setValue(value).addOnCompleteListener { task ->

            // If task was successful, save to local storage and return true. Otherwise, return
            // false for failure.
            if (task.isSuccessful) {
                preferences.edit()
                    .putString(KEY_SCREEN_NAME, value.screenName)
                    .putInt(KEY_AGE, value.age)
                    .putString(KEY_BIO, value.bio)
                    .putString(KEY_IMAGE_RES, value.imageRes)
                    .putStringSet(KEY_BLOCKED_USER_IDS, value.blockedUserIds)
                    .commit()

                emitter.onSuccess(true)
            } else {
                emitter.onSuccess(false)
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    fun deleteAccount() = Single.create<Boolean> { emitter ->
        database.child(userId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                preferences.edit()
                    .remove(KEY_SCREEN_NAME)
                    .remove(KEY_AGE)
                    .remove(KEY_BIO)
                    .remove(KEY_IMAGE_RES)
                    .remove(KEY_BLOCKED_USER_IDS)
                    .commit()

                emitter.onSuccess(true)
            } else {
                emitter.onSuccess(false)
            }
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