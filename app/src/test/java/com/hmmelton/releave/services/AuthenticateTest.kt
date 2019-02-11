package com.hmmelton.releave.services

import com.hmmelton.releave.TestServerHelper
import com.hmmelton.releave.models.User
import com.squareup.moshi.JsonAdapter
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class AuthenticateTest : ApiTest() {

    private lateinit var userAdapter: JsonAdapter<User>

    @Before
    override fun setUp() {
        super.setUp()

        userAdapter = testHelper.moshi.adapter(User::class.java)
    }

    @Test
    fun authenticate_200_success() {
        givenResponseWithAuthHeader(code = 200, responseBody = "")

        val response = execute {
            testHelper.service.authenticate(
                id = TestServerHelper.USER_ID,
                user = userAdapter.fromJson(testHelper.userJson) ?: throw IllegalStateException("userJson is invalid")
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
                id = TestServerHelper.USER_ID,
                user = userAdapter.fromJson(testHelper.userJson) ?: throw IllegalStateException("userJson is invalid")
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
        assertEquals(response.headers().get("Authentication"), TestServerHelper.USER_AUTH_TOKEN)
    }

    private fun thenResponseDoesNotHaveAuthHeader(response: Response<Void>) {
        assertTrue(response.headers().get("Authentication") == null)
    }
}
