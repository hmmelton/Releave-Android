package com.hmmelton.releave.services

import org.junit.Test

class AddUserTest : ApiTest() {

    @Test
    fun addUser_201_success() {
        givenResponse(code = 201, responseBody = "")

        val response = execute { testHelper.service.addUser(user = testHelper.sampleUser) }

        thenCallSuccessful(response = response)
    }

    @Test
    fun addUser_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute { testHelper.service.addUser(user = testHelper.sampleUser) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun addUser_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute { testHelper.service.addUser(user = testHelper.sampleUser) }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }
}
