package com.hmmelton.releave.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.Instant

class InstantTypeAdapter {

    @FromJson fun instantFromString(instantJson: String) = Instant.ofEpochMilli(instantJson.toLong())

    @ToJson fun instantToString(instant: Instant) = instant.toEpochMilli().toString()
}
