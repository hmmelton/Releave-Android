package com.hmmelton.releave.data

import com.hmmelton.releave.data.models.RestroomRequestBody

interface SaveRestroomCallback {
    fun onSaveRestroom(requestBody: RestroomRequestBody)
}
