package com.hmmelton.releave

import com.hmmelton.releave.adapters.InstantTypeAdapter
import com.hmmelton.releave.data.models.AuthRequestBody
import com.hmmelton.releave.data.models.Restroom
import com.hmmelton.releave.data.models.RestroomRequestBody
import com.hmmelton.releave.data.models.User
import com.hmmelton.releave.services.ReleaveService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.threeten.bp.Instant
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class NetworkTestHelper {

    companion object {
        const val CREATED_WHEN: String = "2019-03-05T00:00:00.000Z"
        const val UPDATED_WHEN: String = "2019-03-05T00:00:00.000Z"
        const val USER_AUTH_TOKEN = "123-abc-45de"
        const val USER_EMAIL = "hmmelton@email.com"
        const val USER_FACEBOOK_ID = "123456abc"
        const val USER_FIRST_NAME = "Harrison"
        const val USER_ID = "user-123abc"
        const val USER_LAST_NAME = "Melton"
        const val USER_PAID = true
        const val RESTROOM_ID = "restroom-123abc"
        const val RESTROOM_IS_LOCKED = false
        const val RESTROOM_IS_SINGLE_OCCUPANCY = false
        const val RESTROOM_LAT = 123.45
        const val RESTROOM_LNG = 123.45
        const val RESTROOM_LOCATION = "123 Main St., Seattle, WA 98122"
        const val RESTROOM_NAME = "Starbucks"
        const val RESTROOM_NUM_RATINGS = 5
        const val RESTROOM_RATING = 5.0
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

    /**
     * This function sets the response body of the [MockWebServer], with the Authentication header.
     */
    fun setResponseWithAuthHeader(code: Int, body: String) {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(code)
                .setBody(body)
                .setHeader("Authentication", USER_AUTH_TOKEN)
        )
    }

    val sampleAuthRequestBody = AuthRequestBody(
        facebookId = USER_FACEBOOK_ID,
        firstName = USER_FIRST_NAME,
        lastName = USER_LAST_NAME,
        email = USER_EMAIL
    )

    val sampleRestroomRequestBody = RestroomRequestBody(
        createdBy = USER_ID,
        lat = RESTROOM_LAT,
        lng = RESTROOM_LNG,
        name = RESTROOM_NAME,
        location = RESTROOM_LOCATION,
        isLocked = RESTROOM_IS_LOCKED,
        isSingleOccupancy = RESTROOM_IS_SINGLE_OCCUPANCY,
        rating = RESTROOM_RATING
    )

    val sampleUser = User(
        id = USER_ID,
        facebookId = USER_FACEBOOK_ID,
        firstName = USER_FIRST_NAME,
        lastName = USER_LAST_NAME,
        email = USER_EMAIL,
        paid = USER_PAID,
        createdWhen = Instant.parse(CREATED_WHEN),
        updatedWhen = Instant.parse(UPDATED_WHEN)
    )

    val sampleRestroom = Restroom(
        id = RESTROOM_ID,
        name = RESTROOM_NAME,
        location = RESTROOM_LOCATION,
        lat = RESTROOM_LAT,
        lng = RESTROOM_LNG,
        isLocked = RESTROOM_IS_LOCKED,
        isSingleOccupancy = RESTROOM_IS_SINGLE_OCCUPANCY,
        rating = RESTROOM_RATING,
        numRatings = RESTROOM_NUM_RATINGS,
        createdBy = USER_ID,
        createdWhen = Instant.parse(CREATED_WHEN),
        updatedBy = USER_ID,
        updatedWhen = Instant.parse(UPDATED_WHEN)
    )

    val userJson = """{
        "_id":"$USER_ID",
        "facebook_id":"$USER_FACEBOOK_ID",
        "first_name":"$USER_FIRST_NAME",
        "last_name":"$USER_LAST_NAME",
        "email":"$USER_EMAIL",
        "__v":0,
        "paid":$USER_PAID,
        "created_when":"$CREATED_WHEN",
        "updated_when":"$UPDATED_WHEN"
    }""".trimMargin()

    val restroomJson = """{
        "_id":"$RESTROOM_ID",
        "name":"$RESTROOM_NAME",
        "location":"$RESTROOM_LOCATION",
        "lat":"$RESTROOM_LAT",
        "lng":"$RESTROOM_LNG",
        "is_locked":$RESTROOM_IS_LOCKED,
        "is_single_occupancy":$RESTROOM_IS_SINGLE_OCCUPANCY,
        "rating":$RESTROOM_RATING,
        "num_ratings":$RESTROOM_NUM_RATINGS,
        "created_by":"$USER_ID",
        "created_when":"$CREATED_WHEN",
        "updated_by":"$USER_ID",
        "updated_when":"$UPDATED_WHEN"
    }""".trimIndent()

    val restroomArrayJson = """[
        $restroomJson,
        $restroomJson
    ]""".trimIndent()
}
