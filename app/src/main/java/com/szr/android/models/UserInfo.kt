package com.szr.android.models

data class UserInfo(
    val screenName: String = "",
    val age: Int = 0,
    val bio: String = "",
    val imageRes: String = "",
    val blockedUserIds: Set<String> = emptySet()
)