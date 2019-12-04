package com.releave.android.data.models

import java.io.Serializable

// TODO: add any other relevant values here
data class UserInfo(
    var screenName: String = "",
    var age: Int = 0,
    var bio: String = ""
) : Serializable {

    @Suppress("UNCHECKED_CAST")
    constructor(map: Map<String, Any>) : this(
        screenName = map["screen_name"] as? String ?: "",
        age = (map["age"] as? Long)?.toInt() ?: 100,
        bio = map["bio"] as? String ?: ""
    )

    fun toMap() = mapOf(
        "screen_name" to screenName,
        "age" to age,
        "bio" to bio
    )

    override fun toString(): String {
        return "{ screenName: $screenName, age: $age, bio: $bio }"
    }
}