package com.hmmelton.releave.services

import com.hmmelton.releave.data.models.Restroom
import junit.framework.Assert.assertEquals
import org.junit.Test

class GetAreaRestroomsTest : ApiTest() {

    companion object {
        private const val MIN_LAT = 123.0
        private const val MAX_LAT = 124.0
        private const val MIN_LNG = 123.0
        private const val MAX_LNG = 124.0
    }

    @Test
    fun getAreaRestrooms_200_successWithList() {
        givenResponse(code = 200, responseBody = testHelper.restroomArrayJson)

        val response = execute {
            testHelper.service.getAreaRestrooms(
                minLat = MIN_LAT,
                maxLat = MAX_LAT,
                minLng = MIN_LNG,
                maxLng = MAX_LNG
            )
        }
        val restroomList = response.body()

        thenCallSuccessfulNonNullBody(response = response)
        thenRestroomsParsedCorrectly(restrooms = restroomList ?: throw IllegalStateException("Restroom list is null"))
    }

    @Test
    fun getAreaRestrooms_200_successWithEmptyList() {
        givenResponse(code = 200, responseBody = "[]")

        val response = execute {
            testHelper.service.getAreaRestrooms(
                minLat = MIN_LAT,
                maxLat = MAX_LAT,
                minLng = MIN_LNG,
                maxLng = MAX_LNG
            )
        }
        val restroomList = response.body()

        thenCallSuccessfulNonNullBody(response = response)
        thenRestroomListEmpty(restrooms = restroomList ?: throw IllegalStateException("Restroom list is null"))
    }

    @Test
    fun getAreaRestrooms_401_failure() {
        givenResponse(code = 401, responseBody = ERROR_MESSAGE_401)

        val response = execute {
            testHelper.service.getAreaRestrooms(
                minLat = MIN_LAT,
                maxLat = MAX_LAT,
                minLng = MIN_LNG,
                maxLng = MAX_LNG
            )
        }

        thenCallUnsuccessfulNullBody(
            response = response,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun getAreaRestrooms_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute {
            testHelper.service.getAreaRestrooms(
                minLat = MIN_LAT,
                maxLat = MAX_LAT,
                minLng = MIN_LNG,
                maxLng = MAX_LNG
            )
        }

        thenCallUnsuccessfulNullBody(
            response = response,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }

    private fun thenRestroomsParsedCorrectly(restrooms: List<Restroom>) {
        assertEquals(2, restrooms.count())

        for (restroom in restrooms) {
            assertEquals(testHelper.sampleRestroom, restroom)
        }
    }

    private fun thenRestroomListEmpty(restrooms: List<Restroom>) {
        assertEquals(0, restrooms.count())
    }
}
