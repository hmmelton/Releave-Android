package com.hmmelton.releave.services

import org.junit.Test

class AddRestroomTest : ApiTest() {

    @Test
    fun addRestroom_201_success() {
        givenResponse(code = 201, responseBody = "")

        val response = execute {
            testHelper.service.addRestroom(testHelper.sampleRestroom)
        }

        thenCallSuccessful(response = response)
    }

    @Test
    fun addRestroom_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute {
            testHelper.service.addRestroom(testHelper.sampleRestroom)
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun addRestroom_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute {
            testHelper.service.addRestroom(testHelper.sampleRestroom)
        }

        thenCallUnsuccessful(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }
}
