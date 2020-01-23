package com.releave.android.fragments.signin

import androidx.core.util.PatternsCompat
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.releave.android.DeepLinkUrls
import com.releave.android.R
import com.releave.android.data.models.NotifiableObservable
import com.releave.android.data.models.NotifiableObservableImpl
import io.reactivex.disposables.CompositeDisposable

class SignInViewModel constructor(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    observable: NotifiableObservable = NotifiableObservableImpl()
) : ViewModel(), NotifiableObservable by observable {

    private val disposables = CompositeDisposable()

    val shouldShowEmailScreen: Boolean
        @Bindable get() = (auth.currentUser != null && auth.currentUser?.isEmailVerified == false)

    private val _signInForm = MutableLiveData<SignInFormState>()
    val signInFormState: LiveData<SignInFormState> = _signInForm

    private val _signInResult = MutableLiveData<SignInResult>()
    val signInResult: LiveData<SignInResult> = _signInResult

    private val _action = MutableLiveData<Action>()
    val action: LiveData<Action> = _action

    init {
        (observable as? NotifiableObservableImpl)?.sender = this
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
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
        _action.value = Action.DisplaySpinner
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> handleSignIn()
                    task.exception is FirebaseAuthInvalidUserException -> {
                        // If user does not exist, register
                        registerNewUser(email = email, password = password)
                    }
                    task.exception is FirebaseAuthInvalidCredentialsException -> {
                        // Password was incorrect
                        _action.value = Action.HideSpinner
                        _signInResult.value =
                            SignInResult.Error(message = R.string.error_invalid_credentials)
                    }
                    else -> {
                        // Some other error occurred
                        _action.value = Action.HideSpinner
                        _signInResult.value = SignInResult.Error(message = R.string.error_sign_in)
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
        _action.value = Action.DisplaySpinner

        // Create continuation link to send user back to app after email verification
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl(DeepLinkUrls.EMAIL_VERIFICATION_DEEP_LINK)
            .setAndroidPackageName(DeepLinkUrls.PACKAGE_NAME, true, null)
            .build()

        user.sendEmailVerification(actionCodeSettings).addOnCompleteListener { task ->
            _action.value = Action.HideSpinner
            if (task.isSuccessful) {
                _action.value = Action.DisplayMessage(message = R.string.verification_email_sent)
            } else {
                _action.value = Action.DisplayMessage(message = R.string.verification_email_error)
            }
        }
    }

    fun signOut() {
        auth.signOut()
        notifyPropertyChanged(BR.shouldShowEmailScreen)
    }

    fun displayPasswordResetDialog() {
        _action.value = Action.DisplayPasswordResetDialog
    }

    fun sendResetPasswordEmail(email: String) {
        _action.value = Action.DisplaySpinner

        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl(DeepLinkUrls.EMAIL_RESET_DEEP_LINK)
            .setAndroidPackageName(DeepLinkUrls.PACKAGE_NAME, true, null)
            .build()

        auth.sendPasswordResetEmail(email, actionCodeSettings).addOnCompleteListener { task ->
            _action.value = Action.HideSpinner
            if (task.isSuccessful) {
                _action.value = Action.DisplayMessage(message = R.string.password_reset_email_sent)
            } else {
                _action.value = Action.DisplayMessage(message = R.string.password_reset_email_error)
            }
        }
    }

    private fun handleSignIn() {
        if (auth.currentUser?.isEmailVerified == true) {
            _signInResult.value = SignInResult.Success
        } else {
            // Trigger the layout to display the email verification screen
            _signInResult.value = SignInResult.EmailNotVerified
            notifyPropertyChanged(BR.shouldShowEmailScreen)
        }
    }

    private fun registerNewUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            _action.value = Action.HideSpinner
            if (task.isSuccessful) {
                notifyPropertyChanged(BR.shouldShowEmailScreen)
                sendEmailVerification()
            } else {
                _signInResult.value = SignInResult.Error(message = R.string.error_sign_in)
            }
        }
    }

    // A placeholder username validation check
    private fun isEmailValid(username: String) = PatternsCompat.EMAIL_ADDRESS.matcher(username).matches()

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        // TODO: make some actual requirements
        return password.length > 8
    }

    /**
     * This sealed class is used to send actionable notifications back to Activity.
     */
    sealed class Action {

        class DisplayMessage(val message: Int) : Action()

        object DisplayPasswordResetDialog : Action()

        object HideSpinner : Action()

        object DisplaySpinner : Action()
    }
}
