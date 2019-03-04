package com.hmmelton.releave.services

import com.hmmelton.releave.Secrets
import retrofit2.Retrofit

object ReleaveClient {

    val service: ReleaveService = Retrofit.Builder()
        .baseUrl(Secrets.SERVER_ADDRESS)
        .build()
        .create(ReleaveService::class.java)
}
