package com.releave.android.fragments.signin

/**
 * Data validation state of the login form.
 */
data class SignInFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
