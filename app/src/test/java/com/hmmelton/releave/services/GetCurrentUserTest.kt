package com.hmmelton.releave.services

import com.hmmelton.releave.data.models.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GetCurrentUserTest : ApiTest() {

    @Test
    fun getCurrentUser_200_success() {
        givenResponse(code = 200, responseBody = testHelper.userJson)

        val response = execute { testHelper.service.getCurrentUser() }
        val user = response.body()

        thenCallSuccessfulNonNullBody(response = response, body = user)
        thenUserParsedCorrectly(user = user)
    }

    @Test
    fun getCurrentUser_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute { testHelper.service.getCurrentUser() }
        val user = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = user,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun getCurrentUser_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute { testHelper.service.getCurrentUser() }
        val user = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = user,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }

    private fun thenUserParsedCorrectly(user: User?) {
        assertNotNull(user)
        assertEquals(testHelper.sampleUser, user)
    }
}
