package com.hmmelton.releave.services

import com.hmmelton.releave.NetworkTestHelper
import com.hmmelton.releave.models.User
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class AuthenticateTest : ApiTest() {

    @Test
    fun authenticate_200_success() {
        givenResponseWithAuthHeader(code = 200, responseBody = testHelper.userJson)

        val response = execute {
            testHelper.service.authenticate(
                id = NetworkTestHelper.USER_ID,
                user = testHelper.sampleAuthRequestBody
            )
        }

        thenCallSuccessfulNonNullBody(response = response, body = response.body())
        thenResponseHasAuthHeader(response = response)
        thenUserHasExpectedValues(user = response.body())
    }

    @Test
    fun authenticate_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute {
            testHelper.service.authenticate(
                id = NetworkTestHelper.USER_ID,
                user = testHelper.sampleAuthRequestBody
            )
        }

        thenCallUnsuccessfulNullBody(
            response = response,
            body = response.body(),
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
        thenResponseDoesNotHaveAuthHeader(response = response)
    }

    private fun thenResponseHasAuthHeader(response: Response<User>) {
        assertTrue(response.headers().get("Authentication") != null)
        assertEquals(response.headers().get("Authentication"), NetworkTestHelper.USER_AUTH_TOKEN)
    }

    private fun thenResponseDoesNotHaveAuthHeader(response: Response<User>) {
        assertTrue(response.headers().get("Authentication") == null)
    }

    private fun thenUserHasExpectedValues(user: User?) {
        assertEquals(testHelper.sampleUser, user)
    }
}
