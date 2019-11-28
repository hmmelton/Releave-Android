package com.szr.android.signin

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
import com.szr.android.DeepLinkUrls
import com.szr.android.R
import com.szr.android.addTo
import com.szr.android.data.UserSession
import com.szr.android.data.models.NotifiableObservable
import com.szr.android.data.models.NotifiableObservableImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userSession: UserSession,
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
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
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
            .setAndroidPackageName("com.szr.android", true, null)
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
            .setAndroidPackageName("com.szr.android", true, null)
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
            fetchUserInfo()
                .toSingle()
                .flatMap { info -> userSession.setUserInfo(info) }
                .subscribe { successful ->
                    if (successful) {
                        _signInResult.value = SignInResult.Success
                    } else {
                        auth.signOut()
                        _signInResult.value = SignInResult.Error(R.string.error_sign_in)
                    }
                }
                .addTo(disposables)
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

    private fun fetchUserInfo() = userSession.syncUserInfo()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError {
            auth.signOut()
            _signInResult.value = SignInResult.Error(R.string.error_sign_in)
        }
        .doOnComplete { _signInResult.value = SignInResult.Success }

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

    @Suppress("UNCHECKED_CAST")
    class Factory(private val userSession: UserSession) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SignInViewModel(userSession = userSession) as T
        }
    }
}
