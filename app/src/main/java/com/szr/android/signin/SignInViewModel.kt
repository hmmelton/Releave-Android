package com.szr.android.signin

import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.szr.android.R
import com.szr.android.models.NotifiableObservable
import com.szr.android.models.NotifiableObservableImpl
import com.szr.android.utils.DeepLinks

class SignInViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    observable: NotifiableObservable = NotifiableObservableImpl()
) : ViewModel(), NotifiableObservable by observable {

    val shouldShowEmailScreen: Boolean
        @Bindable get() = (auth.currentUser != null && auth.currentUser?.isEmailVerified == false)

    private val _signInForm = MutableLiveData<SignInFormState>()
    val signInFormState: LiveData<SignInFormState> = _signInForm

    private val _signInResult = MutableLiveData<SignInResult>()
    val signInResult: LiveData<SignInResult> = _signInResult

    init {
        (observable as? NotifiableObservableImpl)?.sender = this
    }

    fun start() {
        if (auth.currentUser != null) {
            auth.currentUser?.reload()?.addOnCompleteListener {
                if (auth.currentUser?.isEmailVerified == true) {
                    _signInResult.value = SignInResult.Success
                }
            }
        }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    if (auth.currentUser?.isEmailVerified == true) {
                        _signInResult.value = SignInResult.Success
                    } else {
                        _signInResult.value = SignInResult.EmailNotVerified
                        notifyPropertyChanged(BR.shouldShowEmailScreen)
                    }
                }
                task.exception is FirebaseAuthInvalidUserException -> {
                    // If user does not exist, register
                    registerNewUser(email = email, password = password)
                }
                task.exception is FirebaseAuthInvalidCredentialsException -> {
                    // Password was incorrect
                    _signInResult.value =
                        SignInResult.Error(message = R.string.error_invalid_credentials)
                }
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isEmailValid(username)) {
            _signInForm.value = SignInFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _signInForm.value = SignInFormState(passwordError = R.string.invalid_password)
        } else {
            _signInForm.value = SignInFormState(isDataValid = true)
        }
    }

    fun sendEmailVerification() {
        val user = auth.currentUser ?: return
        val url = DeepLinks.getEmailDeepLink(user.uid)
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl(url)
            .setAndroidPackageName("com.szr.android", true, null)
            .build()

        user.sendEmailVerification(actionCodeSettings).addOnCompleteListener {
        }
    }

    fun signOut() {
        auth.signOut()
        notifyPropertyChanged(BR.shouldShowEmailScreen)
    }

    private fun registerNewUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                notifyPropertyChanged(BR.shouldShowEmailScreen)
                sendEmailVerification()
            } else {
                _signInResult.value = SignInResult.Error(message = R.string.error_sign_in)
            }
        }
    }

    // A placeholder username validation check
    private fun isEmailValid(username: String) = Patterns.EMAIL_ADDRESS.matcher(username).matches()

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        // TODO: make some actual requirements
        return password.length > 8
    }
}
