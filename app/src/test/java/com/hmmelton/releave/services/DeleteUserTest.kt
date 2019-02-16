package com.hmmelton.releave.services

import com.hmmelton.releave.NetworkTestHelper
import org.junit.Test

class DeleteUserTest : ApiTest() {

    companion object {
        private const val ERROR_MESSAGE_404 = """{ "error": "User not found" }"""
    }

    @Test
    fun deleteUser_200_success() {
        givenResponse(code = 200, responseBody = "")

        val response = execute { testHelper.service.deleteUser(id = NetworkTestHelper.USER_ID) }

        thenCallSuccessful(response = response)
    }

    @Test
    fun deleteUser_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute { testHelper.service.deleteUser(id = NetworkTestHelper.USER_ID) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun deleteUser_404_failure() {
        givenResponse(code = 404, responseBody = ERROR_MESSAGE_404)

        val response = execute { testHelper.service.deleteUser(id = NetworkTestHelper.USER_ID) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 404,
            expectedErrorMessage = ERROR_MESSAGE_404
        )
    }

    @Test
    fun deleteUser_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute { testHelper.service.deleteUser(id = NetworkTestHelper.USER_ID) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }
}
