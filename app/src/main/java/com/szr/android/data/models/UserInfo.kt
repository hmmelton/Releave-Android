package com.szr.android.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserInfo(
    @SerializedName("screen_name")
    var screenName: String = "",
    var age: Int = 0,
    var bio: String = "",
    @SerializedName("image_res")
    var imageRes: String = "",
    @SerializedName("blocked_user_ids")
    var blockedUserIds: Set<String> = emptySet()
) : Serializable