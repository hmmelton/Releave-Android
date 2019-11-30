package com.szr.android.data.models

import java.io.Serializable

data class UserInfo(
    var screenName: String = "",
    var age: Int = 0,
    var bio: String = "",
    var imageRes: String = "",
    var blockedUserIds: Set<String> = emptySet()
) : Serializable {

    @Suppress("UNCHECKED_CAST")
    constructor(map: Map<String, Any>) : this(
        screenName = map["screen_name"] as? String ?: "",
        age = map["age"] as? Int ?: 0,
        bio = map["bio"] as? String ?: "",
        imageRes = map["image_res"] as? String ?: "",
        blockedUserIds = map["blocked_user_ids"] as? Set<String> ?: emptySet()
    )

    fun toMap() = mapOf(
        "screen_name" to screenName,
        "age" to age,
        "bio" to bio,
        "image_res" to imageRes,
        "blocked_user_ids" to blockedUserIds.toList()
    )

    override fun toString(): String {
        return "{ screenName: $screenName, age: $age, bio: $bio, imageRes: $imageRes," +
                "blockedUserIds: $blockedUserIds }"
    }
}