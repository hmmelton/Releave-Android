package com.ambush.android.signin

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ambush.android.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class SignInViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _loginForm = MutableLiveData<SignInFormState>()
    val signInFormState: LiveData<SignInFormState> = _loginForm

    private val _loginResult = MutableLiveData<SignInResult>()
    val signInResult: LiveData<SignInResult> = _loginResult

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> _loginResult.value = SignInResult.SUCCESS
                task.exception is FirebaseAuthInvalidUserException -> {
                    // If user does not exist, register
                    registerNewUser(email = email, password = password)
                }
                else -> {
                    // If error is not related to absent user, post failure
                    Log.e("SignInViewModel", task.exception.toString())
                    _loginResult.value = SignInResult.ERROR
                }
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isEmailValid(username)) {
            _loginForm.value = SignInFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = SignInFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = SignInFormState(isDataValid = true)
        }
    }

    private fun registerNewUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _loginResult.value = SignInResult.SUCCESS
            } else {
                _loginResult.value = SignInResult.ERROR
            }
        }
    }

    // A placeholder username validation check
    private fun isEmailValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        // TODO: make some actual requirements
        return password.length > 8
    }
}
