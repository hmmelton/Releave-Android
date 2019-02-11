package com.hmmelton.releave.services

import com.hmmelton.releave.TestServerHelper
import com.hmmelton.releave.models.Restroom
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

        thenCallSuccessfulNonNullBody(response = response, body = restroomList)
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

        thenCallSuccessfulNonNullBody(response = response, body = restroomList)
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
        val restroomList = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = restroomList,
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
        val restroomList = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = restroomList,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }

    private fun thenRestroomsParsedCorrectly(restrooms: List<Restroom>) {
        assertEquals(2, restrooms.count())

        for (restroom in restrooms) {
            assertEquals(TestServerHelper.RESTROOM_ID, restroom.id)
            assertEquals(TestServerHelper.RESTROOM_NAME, restroom.name)
            assertEquals(TestServerHelper.RESTROOM_LOCATION, restroom.location)
            assertEquals(TestServerHelper.RESTROOM_LAT, restroom.lat)
            assertEquals(TestServerHelper.RESTROOM_LNG, restroom.lng)
            assertEquals(TestServerHelper.RESTROOM_LNG, restroom.lng)
            assertEquals(TestServerHelper.RESTROOM_IS_LOCKED, restroom.isLocked)
            assertEquals(TestServerHelper.RESTROOM_RATING, restroom.getRating())
            assertEquals(TestServerHelper.RESTROOM_NUM_RATINGS, restroom.getNumRatings())
            assertEquals(TestServerHelper.USER_ID, restroom.createdBy)
            assertEquals(TestServerHelper.CREATED_WHEN, restroom.createdWhen)
            assertEquals(TestServerHelper.USER_ID, restroom.updatedBy)
            assertEquals(TestServerHelper.UPDATED_WHEN, restroom.updatedWhen)
        }
    }

    private fun thenRestroomListEmpty(restrooms: List<Restroom>) {
        assertEquals(0, restrooms.count())
    }
}
