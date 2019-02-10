package com.hmmelton.releave.services

import com.hmmelton.releave.TestServerHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import retrofit2.Call
import retrofit2.Response

abstract class ApiTest {

    companion object {
        const val ERROR_MESSAGE_401 = """{ "error": "No authorization token found" }"""
        const val ERROR_MESSAGE_500 = """{ "error": "Internal server error" }"""
    }

    protected lateinit var testHelper: TestServerHelper

    @Before
    open fun setUp() {
        testHelper = TestServerHelper()
    }

    protected fun <T> execute(action: () -> Call<T>): Response<T> {
        val call: Call<T> = action()
        return call.execute()
    }

    protected fun givenResponse(code: Int, responseBody: String) {
        testHelper.setResponse(code = code, body = responseBody)
    }

    protected fun thenCallSuccessful(response: Response<Void>) {
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
    }

    protected fun <T> thenCallSuccessfulNonNullBody(response: Response<T>, body: T?) {
        assertTrue(response.isSuccessful)
        assertEquals(200, response.code())
        assertNotNull(body)
    }

    protected fun thenCallUnsuccessful(
        response: Response<Void>,
        expectedResponseCode: Int,
        expectedErrorMessage: String
    ) {
        assertFalse(response.isSuccessful)
        assertEquals(expectedResponseCode, response.code())
        assertEquals(expectedErrorMessage, response.errorBody()?.string())
    }

    protected fun <T> thenCallUnsuccessfulNullBody(
        response: Response<T>,
        body: T?,
        expectedResponseCode: Int,
        expectedErrorMessage: String
    ) {
        assertFalse(response.isSuccessful)
        assertEquals(expectedResponseCode, response.code())
        assertNull(body)
        assertEquals(expectedErrorMessage, response.errorBody()?.string())
    }
}
