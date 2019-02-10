package com.hmmelton.releave.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.Instant

class InstantTypeAdapter {

    @FromJson fun instantFromJson(instantJson: String) = Instant.parse(instantJson)

    @ToJson fun instantToJson(instant: Instant) = instant.toString()
}
