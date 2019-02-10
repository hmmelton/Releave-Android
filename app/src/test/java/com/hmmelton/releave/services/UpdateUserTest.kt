package com.hmmelton.releave.services

import com.hmmelton.releave.TestServerHelper
import com.hmmelton.releave.models.User
import com.squareup.moshi.JsonAdapter
import org.junit.Before
import org.junit.Test

class UpdateUserTest : ApiTest() {

    private lateinit var userAdapter: JsonAdapter<User>

    @Before
    override fun setUp() {
        super.setUp()

        userAdapter = testHelper.moshi.adapter(User::class.java)
    }

    @Test
    fun updateUser_200_success() {
        givenResponse(code = 200, responseBody = "")

        val response = execute {
            testHelper.service.updateUser(
                id = TestServerHelper.FACEBOOK_ID,
                user = userAdapter.fromJson(testHelper.userJson) ?: throw IllegalStateException("userJson is null")
            )
        }

        thenCallSuccessful(response = response)
    }

    @Test
    fun updateUser_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute {
            testHelper.service.updateUser(
                id = TestServerHelper.FACEBOOK_ID,
                user = userAdapter.fromJson(testHelper.userJson) ?: throw IllegalStateException("userJson is null")
            )
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun updateUser_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute {
            testHelper.service.updateUser(
                id = TestServerHelper.FACEBOOK_ID,
                user = userAdapter.fromJson(testHelper.userJson) ?: throw IllegalStateException("userJson is null")
            )
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }
}
