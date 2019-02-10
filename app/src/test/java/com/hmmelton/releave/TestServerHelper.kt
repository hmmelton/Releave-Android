package com.hmmelton.releave

import com.hmmelton.releave.adapters.InstantTypeAdapter
import com.hmmelton.releave.services.ReleaveService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.threeten.bp.Instant
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class TestServerHelper {

    companion object {
        const val AUTH_TOKEN = "123-abc-45de"
        val CREATED_WHEN: Instant = Instant.parse("2019-01-02T00:00:00Z")
        const val EMAIL = "hmmelton@email.com"
        const val FACEBOOK_ID = "123456abc"
        const val FIRST_NAME = "Harrison"
        const val LAST_NAME = "Melton"
        const val PAID = true
    }

    val service: ReleaveService
    val moshi = Moshi.Builder()
        .add(InstantTypeAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val mockWebServer = MockWebServer()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("").toString())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        service = retrofit.create(ReleaveService::class.java)
    }

    /**
     * This function sets the response body of the [MockWebServer].
     */
    fun setResponse(code: Int, body: String) {
        mockWebServer.enqueue(MockResponse().setResponseCode(code).setBody(body))
    }

    val userJson = """{
        "created_when":"$CREATED_WHEN",
        "facebook_id":"$FACEBOOK_ID",
        "first_name":"$FIRST_NAME",
        "last_name":"$LAST_NAME",
        "email":"$EMAIL",
        "auth_token":"$AUTH_TOKEN",
        "paid":$PAID
    }""".trimMargin()
}
