package com.hmmelton.releave.services

import com.hmmelton.releave.TestServerHelper
import com.hmmelton.releave.models.User
import org.junit.Assert.assertEquals
import org.junit.Test

class GetUserTest : ApiTest() {

    companion object {
        private const val ERROR_MESSAGE_404 = """{ "error": "User not found" }"""
    }

    @Test
    fun getUser_200_success() {
        givenResponse(code = 200, responseBody = testHelper.userJson)

        val response = execute { testHelper.service.getUser(id = TestServerHelper.USER_ID) }
        val user = response.body()

        thenCallSuccessfulNonNullBody(response = response, body = user)
        thenUserParsedCorrectly(user = user ?: throw IllegalStateException("User is null"))
    }

    @Test
    fun getUser_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute { testHelper.service.getUser(TestServerHelper.USER_ID) }
        val user = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = user,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun getUser_404_failure() {
        givenResponse(code = 404, responseBody = ERROR_MESSAGE_404)

        val response = execute { testHelper.service.getUser(TestServerHelper.USER_ID) }
        val user = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = user,
            expectedResponseCode = 404,
            expectedErrorMessage = ERROR_MESSAGE_404
        )
    }

    @Test
    fun getUser_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute { testHelper.service.getUser(TestServerHelper.USER_ID) }
        val user = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = user,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }

    private fun thenUserParsedCorrectly(user: User) {
        assertEquals(TestServerHelper.USER_ID, user.id)
        assertEquals(TestServerHelper.CREATED_WHEN, user.createdWhen)
        assertEquals(TestServerHelper.USER_FACEBOOK_ID, user.facebookId)
        assertEquals(TestServerHelper.USER_FIRST_NAME, user.firstName)
        assertEquals(TestServerHelper.USER_LAST_NAME, user.lastName)
        assertEquals(TestServerHelper.USER_EMAIL, user.email)
        assertEquals(TestServerHelper.USER_AUTH_TOKEN, user.authToken)
        assertEquals(TestServerHelper.USER_PAID, user.paid)
    }
}
