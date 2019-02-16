package com.hmmelton.releave.services

import com.hmmelton.releave.NetworkTestHelper
import org.junit.Test

class UpdateUserTest : ApiTest() {

    @Test
    fun updateUser_200_success() {
        givenResponse(code = 200, responseBody = "")

        val response = execute {
            testHelper.service.updateUser(
                id = NetworkTestHelper.USER_ID,
                user = testHelper.sampleUser
            )
        }

        thenCallSuccessful(response = response)
    }

    @Test
    fun updateUser_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute {
            testHelper.service.updateUser(
                id = NetworkTestHelper.USER_ID,
                user = testHelper.sampleUser
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
                id = NetworkTestHelper.USER_ID,
                user = testHelper.sampleUser
            )
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }
}
