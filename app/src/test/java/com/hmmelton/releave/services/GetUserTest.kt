package com.hmmelton.releave.services

import com.hmmelton.releave.NetworkTestHelper
import com.hmmelton.releave.data.models.User
import org.junit.Assert.assertEquals
import org.junit.Test

class GetUserTest : ApiTest() {

    companion object {
        private const val ERROR_MESSAGE_404 = """{ "error": "User not found" }"""
    }

    @Test
    fun getUser_200_success() {
        givenResponse(code = 200, responseBody = testHelper.userJson)

        val response = execute { testHelper.service.getUser(id = NetworkTestHelper.USER_ID) }
        val user = response.body()

        thenCallSuccessfulNonNullBody(response = response)
        thenUserParsedCorrectly(user = user ?: throw IllegalStateException("User is null"))
    }

    @Test
    fun getUser_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute { testHelper.service.getUser(NetworkTestHelper.USER_ID) }

        thenCallUnsuccessfulNullBody(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun getUser_404_failure() {
        givenResponse(code = 404, responseBody = ERROR_MESSAGE_404)

        val response = execute { testHelper.service.getUser(NetworkTestHelper.USER_ID) }

        thenCallUnsuccessfulNullBody(
            response = response,
            expectedResponseCode = 404,
            expectedErrorMessage = ERROR_MESSAGE_404
        )
    }

    @Test
    fun getUser_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute { testHelper.service.getUser(NetworkTestHelper.USER_ID) }

        thenCallUnsuccessfulNullBody(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }

    private fun thenUserParsedCorrectly(user: User) {
        assertEquals(testHelper.sampleUser, user)
    }
}
