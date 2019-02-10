package com.hmmelton.releave.services

import com.hmmelton.releave.TestServerHelper
import com.hmmelton.releave.models.User
import com.squareup.moshi.JsonAdapter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetUserTest : ApiTest() {

    companion object {
        private const val ERROR_MESSAGE_401 = """{ "message": "Not authorized" }"""
        private const val ERROR_MESSAGE_404 = """{ "message": "User not found" }"""
        private const val ERROR_MESSAGE_500 = """{ "message": "Internal server error" }"""
    }

    private lateinit var userAdapter: JsonAdapter<User>

    @Before
    override  fun setUp() {
        super.setUp()

        userAdapter = testHelper.moshi.adapter(User::class.java)
    }

    @Test
    fun getUser_200_success() {
        givenResponse(code = 200, responseBody = testHelper.userJson)

        val response = execute { testHelper.service.getUser(TestServerHelper.FACEBOOK_ID) }
        val user = response.body()

        thenCallSuccessfulNonNullBody(response = response, body = user)
        thenUserParsedCorrectly(user = user ?: throw IllegalStateException("User is null"))
    }

    @Test
    fun getUser_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute { testHelper.service.getUser(TestServerHelper.FACEBOOK_ID) }
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

        val response = execute { testHelper.service.getUser(TestServerHelper.FACEBOOK_ID) }
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

        val response = execute { testHelper.service.getUser(TestServerHelper.FACEBOOK_ID) }
        val user = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = user,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }

    private fun thenUserParsedCorrectly(user: User) {
        assertEquals(TestServerHelper.CREATED_WHEN, user.createdWhen)
        assertEquals(TestServerHelper.FACEBOOK_ID, user.facebookId)
        assertEquals(TestServerHelper.FIRST_NAME, user.firstName)
        assertEquals(TestServerHelper.LAST_NAME, user.lastName)
        assertEquals(TestServerHelper.EMAIL, user.email)
        assertEquals(TestServerHelper.AUTH_TOKEN, user.authToken)
        assertEquals(TestServerHelper.PAID, user.paid)
    }
}
