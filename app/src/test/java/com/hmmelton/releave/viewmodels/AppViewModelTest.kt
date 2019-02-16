package com.hmmelton.releave.viewmodels

import android.content.SharedPreferences
import com.hmmelton.releave.models.User
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import java.time.Instant

class AppViewModelTest {

    companion object {
        private const val SERVER_ADDRESS = "www.test.com/"
        val CREATED_WHEN: Instant = Instant.parse("2019-01-02T00:00:00Z")
        const val USER_AUTH_TOKEN = "123-abc-45de"
        const val USER_EMAIL = "hmmelton@email.com"
        const val USER_FACEBOOK_ID = "123456abc"
        const val USER_FIRST_NAME = "Harrison"
        const val USER_ID = "user-123abc"
        const val USER_LAST_NAME = "Melton"
        const val USER_PAID = true
    }

    private val userJson = """{
        "id":"$USER_ID",
        "created_when":"$CREATED_WHEN",
        "facebook_id":"$USER_FACEBOOK_ID",
        "first_name":"$USER_FIRST_NAME",
        "last_name":"$USER_LAST_NAME",
        "email":"$USER_EMAIL",
        "auth_token":"$USER_AUTH_TOKEN",
        "paid":$USER_PAID
    }""".trimMargin()

    private val sampleUser = User(
        id = USER_ID,
        createdWhen = CREATED_WHEN,
        facebookId = USER_FACEBOOK_ID,
        firstName = USER_FIRST_NAME,
        lastName = USER_LAST_NAME,
        email = USER_EMAIL,
        authToken = USER_AUTH_TOKEN,
        paid = USER_PAID
    )

    private lateinit var preferences: SharedPreferences
    private lateinit var preferencesEditor: SharedPreferences.Editor
    private lateinit var viewModel: AppViewModel

    @Before
    fun setUp() {
        preferencesEditor = mock()
        preferences = mock {
            on { edit() } doReturn preferencesEditor
        }
        viewModel = AppViewModel(preferences = preferences, serverAddress = SERVER_ADDRESS)
    }

    @Test
    fun getCurrentUser_currentUserNonNull_userParsedFromJson() {
        givenCurrentUserDoesNotReturnNull()

        val user = viewModel.currentUser

        thenUserParsedCorrectly(user = user)
    }

    @Test
    fun getCurrentUser_currentUserNull_nullUserReturned() {
        givenCurrentUserReturnsNull()

        val user = viewModel.currentUser

        thenUserIsNull(user = user)
    }

    private fun givenCurrentUserReturnsNull() {
        whenever(preferences.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).doReturn("")
    }

    private fun givenCurrentUserDoesNotReturnNull() {
        whenever(preferences.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).doReturn(userJson)
    }

    private fun thenUserParsedCorrectly(user: User?) {
        assertNotNull(user)
        assertEquals(sampleUser, user)
    }

    private fun thenUserIsNull(user: User?) {
        assertNull(user)
    }
}
