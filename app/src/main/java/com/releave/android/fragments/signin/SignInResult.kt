package com.releave.android.fragments.signin

/**
 * Class used to signal whether login request was successful or not
 */
sealed class SignInResult {
    object Success : SignInResult()

    object EmailNotVerified : SignInResult()

    class Error(val message: Int) : SignInResult()
}