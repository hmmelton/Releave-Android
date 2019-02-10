package com.hmmelton.releave.services

import com.hmmelton.releave.TestServerHelper
import com.hmmelton.releave.models.User
import com.squareup.moshi.JsonAdapter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetCurrentUserTest : ApiTest() {

    private lateinit var userAdapter: JsonAdapter<User>

    @Before
    override fun setUp() {
        super.setUp()

        userAdapter = testHelper.moshi.adapter(User::class.java)
    }

    @Test
    fun getCurrentUser_200_success() {
        givenResponse(code = 200, responseBody = testHelper.userJson)

        val response = execute { testHelper.service.getCurrentUser() }
        val user = response.body()

        thenCallSuccessfulNonNullBody(response = response, body = user)
        thenUserParsedCorrectly(user = user ?: throw IllegalStateException("User is null"))
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

    private fun thenUserParsedCorrectly(user: User) {
        Assert.assertEquals(TestServerHelper.CREATED_WHEN, user.createdWhen)
        Assert.assertEquals(TestServerHelper.FACEBOOK_ID, user.facebookId)
        Assert.assertEquals(TestServerHelper.FIRST_NAME, user.firstName)
        Assert.assertEquals(TestServerHelper.LAST_NAME, user.lastName)
        Assert.assertEquals(TestServerHelper.EMAIL, user.email)
        Assert.assertEquals(TestServerHelper.AUTH_TOKEN, user.authToken)
        Assert.assertEquals(TestServerHelper.PAID, user.paid)
    }
}
