package com.hmmelton.releave.services

import com.hmmelton.releave.Secrets
import com.hmmelton.releave.adapters.InstantTypeAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ReleaveClient {

    private val moshi = Moshi.Builder()
        .add(InstantTypeAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: ReleaveService = Retrofit.Builder()
        .baseUrl(Secrets.SERVER_ADDRESS)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(ReleaveService::class.java)
}
