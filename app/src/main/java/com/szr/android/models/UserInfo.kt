package com.szr.android.models

data class UserInfo(
    var screenName: String = "",
    var age: Int = 0,
    var bio: String = "",
    var imageRes: String = "",
    var blockedUserIds: Set<String> = emptySet()
)