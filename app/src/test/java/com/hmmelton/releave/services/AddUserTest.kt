package com.hmmelton.releave.services

import com.hmmelton.releave.models.User
import com.squareup.moshi.JsonAdapter
import org.junit.Before
import org.junit.Test

class AddUserTest : ApiTest() {

    companion object {
        private const val ERROR_MESSAGE_401 = """{ "message": "Not authorized" }"""
        private const val ERROR_MESSAGE_500 = """{ "message": "Internal server error" }"""
    }

    private lateinit var userAdapter: JsonAdapter<User>

    @Before
    override fun setUp() {
        super.setUp()

        userAdapter = testHelper.moshi.adapter(User::class.java)
    }

    @Test
    fun addUser_200_success() {
        givenResponse(code = 200, responseBody = testHelper.userJson)

        val sampleUser = userAdapter.fromJson(testHelper.userJson) ?: throw IllegalStateException("userJson is invalid")
        val response = execute { testHelper.service.addUser(user = sampleUser) }

        thenCallSuccessful(response = response)
    }

    @Test
    fun addUser_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val sampleUser = userAdapter.fromJson(testHelper.userJson) ?: throw IllegalStateException("userJson is invalid")
        val response = execute { testHelper.service.addUser(user = sampleUser) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun addUser_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val sampleUser = userAdapter.fromJson(testHelper.userJson) ?: throw IllegalStateException("userJson is invalid")
        val response = execute { testHelper.service.addUser(user = sampleUser) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }
}