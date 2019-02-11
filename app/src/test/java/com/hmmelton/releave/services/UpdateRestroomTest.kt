package com.hmmelton.releave.services

import com.hmmelton.releave.TestServerHelper
import com.hmmelton.releave.models.Restroom
import com.squareup.moshi.JsonAdapter
import org.junit.Before
import org.junit.Test

class UpdateRestroomTest : ApiTest() {

    companion object {
        private const val ERROR_MESSAGE_404 = """{ "error": "Restroom not found" }"""
    }

    private lateinit var restroomAdapter: JsonAdapter<Restroom>

    @Before
    override fun setUp() {
        super.setUp()

        restroomAdapter = testHelper.moshi.adapter(Restroom::class.java)
    }

    @Test
    fun updateRestroom_200_success() {
        givenResponse(code = 200, responseBody = "")

        val response = execute {
            testHelper.service.updateRestroom(
                id = TestServerHelper.RESTROOM_ID,
                restroom = restroomAdapter.fromJson(testHelper.restroomJson)
                    ?: throw IllegalStateException("restroomJson not valid")
            )
        }

        thenCallSuccessful(response = response)
    }

    @Test
    fun updateRestroom_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute {
            testHelper.service.updateRestroom(
                id = TestServerHelper.RESTROOM_ID,
                restroom = restroomAdapter.fromJson(testHelper.restroomJson)
                    ?: throw IllegalStateException("restroomJson not valid")
            )
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun updateRestroom_404_failure() {
        givenResponse(code = 404, responseBody = ERROR_MESSAGE_404)

        val response = execute {
            testHelper.service.updateRestroom(
                id = TestServerHelper.RESTROOM_ID,
                restroom = restroomAdapter.fromJson(testHelper.restroomJson)
                    ?: throw IllegalStateException("restroomJson not valid")
            )
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 404,
            expectedErrorMessage = ERROR_MESSAGE_404
        )
    }

    @Test
    fun updateRestroom_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute {
            testHelper.service.updateRestroom(
                id = TestServerHelper.RESTROOM_ID,
                restroom = restroomAdapter.fromJson(testHelper.restroomJson)
                    ?: throw IllegalStateException("restroomJson not valid")
            )
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }
}