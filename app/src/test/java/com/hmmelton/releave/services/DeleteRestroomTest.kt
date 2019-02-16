package com.hmmelton.releave.services

import com.hmmelton.releave.NetworkTestHelper
import org.junit.Test

class DeleteRestroomTest : ApiTest() {

    companion object {
        private const val ERROR_MESSAGE_404 = """{ "error": "User not found" }"""
    }

    @Test
    fun deleteRestroom_200_success() {
        givenResponse(code = 200, responseBody = "")

        val response = execute { testHelper.service.deleteRestroom(id = NetworkTestHelper.RESTROOM_ID) }

        thenCallSuccessful(response = response)
    }

    @Test
    fun deleteRestroom_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute { testHelper.service.deleteRestroom(id = NetworkTestHelper.RESTROOM_ID) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun deleteRestroom_404_failure() {
        givenResponse(code = 404, responseBody = ERROR_MESSAGE_404)

        val response = execute { testHelper.service.deleteRestroom(id = NetworkTestHelper.RESTROOM_ID) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 404,
            expectedErrorMessage = ERROR_MESSAGE_404
        )
    }

    @Test
    fun deleteRestroom_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute { testHelper.service.deleteRestroom(id = NetworkTestHelper.RESTROOM_ID) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }
}
