package com.hmmelton.releave.services

import com.hmmelton.releave.NetworkTestHelper
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class AuthenticateTest : ApiTest() {

    @Test
    fun authenticate_200_success() {
        givenResponseWithAuthHeader(code = 200, responseBody = "")

        val response = execute {
            testHelper.service.authenticate(
                id = NetworkTestHelper.USER_ID,
                user = testHelper.sampleUser
            )
        }

        thenCallSuccessful(response = response)
        thenResponseHasAuthHeader(response = response)
    }

    @Test
    fun authenticate_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute {
            testHelper.service.authenticate(
                id = NetworkTestHelper.USER_ID,
                user = testHelper.sampleUser
            )
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
        thenResponseDoesNotHaveAuthHeader(response = response)
    }

    private fun thenResponseHasAuthHeader(response: Response<Void>) {
        assertTrue(response.headers().get("Authentication") != null)
        assertEquals(response.headers().get("Authentication"), NetworkTestHelper.USER_AUTH_TOKEN)
    }

    private fun thenResponseDoesNotHaveAuthHeader(response: Response<Void>) {
        assertTrue(response.headers().get("Authentication") == null)
    }
}
