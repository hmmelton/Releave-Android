package com.hmmelton.releave.services

import com.hmmelton.releave.models.Restroom
import com.squareup.moshi.JsonAdapter
import org.junit.Before
import org.junit.Test

class AddRestroomTest : ApiTest() {

    private lateinit var restroomAdapter: JsonAdapter<Restroom>

    @Before
    override fun setUp() {
        super.setUp()

        restroomAdapter = testHelper.moshi.adapter(Restroom::class.java)
    }

    @Test
    fun addRestroom_201_success() {
        givenResponse(code = 201, responseBody = "")

        val response = execute {
            testHelper.service.addRestroom(
                restroomAdapter.fromJson(testHelper.restroomJson)
                    ?: throw IllegalStateException("restroomJson is invalid")
            )
        }

        thenCallSuccessful(response = response)
    }

    @Test
    fun addRestroom_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute {
            testHelper.service.addRestroom(
                restroomAdapter.fromJson(testHelper.restroomJson)
                    ?: throw IllegalStateException("restroomJson is invalid")
            )
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun addRestroom_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute {
            testHelper.service.addRestroom(
                restroomAdapter.fromJson(testHelper.restroomJson)
                    ?: throw IllegalStateException("restroomJson is invalid")
            )
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }
}
