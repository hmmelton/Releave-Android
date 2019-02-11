package com.hmmelton.releave.services

import com.hmmelton.releave.TestServerHelper
import com.hmmelton.releave.models.Restroom
import junit.framework.Assert.assertEquals
import org.junit.Test

class GetRestroomTest : ApiTest() {

    companion object {
        private const val ERROR_MESSAGE_404 = """{ "message": "Restroom not found" }"""
    }

    @Test
    fun getRestroom_200_success() {
        givenResponse(code = 200, responseBody = testHelper.restroomJson)

        val response = execute { testHelper.service.getRestroom(TestServerHelper.RESTROOM_ID) }
        val restroom = response.body()

        thenCallSuccessfulNonNullBody(
            response = response,
            body = restroom
        )
        thenRestroomParsedCorrectly(restroom = restroom ?: throw IllegalStateException("Restroom is null"))
    }

    @Test
    fun getRestroom_401_failure() {
        givenResponse(code = 401, responseBody = testHelper.restroomJson)

        val response = execute { testHelper.service.getRestroom(TestServerHelper.RESTROOM_ID) }
        val restroom = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = restroom,
            expectedResponseCode = 401,
            expectedErrorMessage = ERROR_MESSAGE_401
        )
    }

    @Test
    fun getRestroom_404_failure() {
        givenResponse(code = 404, responseBody = ERROR_MESSAGE_404)

        val response = execute { testHelper.service.getRestroom(TestServerHelper.RESTROOM_ID) }
        val restroom = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = restroom,
            expectedResponseCode = 404,
            expectedErrorMessage = ERROR_MESSAGE_404
        )
    }

    @Test
    fun getRestroom_500_failure() {
        givenResponse(code = 500, responseBody = ERROR_MESSAGE_500)

        val response = execute { testHelper.service.getRestroom(TestServerHelper.RESTROOM_ID) }
        val restroom = response.body()

        thenCallUnsuccessfulNullBody(
            response = response,
            body = restroom,
            expectedResponseCode = 500,
            expectedErrorMessage = ERROR_MESSAGE_500
        )
    }

    private fun thenRestroomParsedCorrectly(restroom: Restroom) {
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
