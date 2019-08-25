package com.szr.android.views.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.szr.android.R
import com.szr.android.databinding.ActivitySignInBinding
import com.szr.android.signin.SignInFormState
import com.szr.android.signin.SignInResult
import com.szr.android.signin.SignInViewModel
import com.szr.android.views.PasswordResetDialog
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var signInViewModel: SignInViewModel

    private val signInFormStateObserver: Observer<SignInFormState> = Observer {
        val loginState = it ?: return@Observer

        // Disable login button unless both username / password is valid
        login.isEnabled = loginState.isDataValid

        if (loginState.usernameError != null) {
            email.error = getString(loginState.usernameError)
        }
        if (loginState.passwordError != null) {
            password.error = getString(loginState.passwordError)
        }
    }

    private val signInResultObserver: Observer<SignInResult> = Observer {
        val signInResult = it ?: return@Observer

        loading.visibility = View.GONE
        when (signInResult) {
            is SignInResult.Error -> showLoginFailed(signInResult.message)
            is SignInResult.Success -> navigateToMainScreen()
            else -> {}
        }
    }

    private val actionObserver: Observer<SignInViewModel.Action> = Observer { action ->
        if (action == null) return@Observer

        when (action) {
            is SignInViewModel.Action.DisplayMessage -> {
                Toast.makeText(
                    applicationContext,
                    action.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            SignInViewModel.Action.DisplayPasswordResetDialog -> {
                // Display dialog prompting user to enter email for password reset
                PasswordResetDialog().apply {
                    callback = { email -> signInViewModel.sendResetPasswordEmail(email = email) }
                }.show(supportFragmentManager, "PasswordResetDialog")
            }
            SignInViewModel.Action.HideSpinner -> loading.visibility = View.GONE
            SignInViewModel.Action.DisplaySpinner -> loading.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivitySignInBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_sign_in
        )

        signInViewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java).apply {
            signInFormState.observe(this@SignInActivity, signInFormStateObserver)
            signInResult.observe(this@SignInActivity, signInResultObserver)
            action.observe(this@SignInActivity, actionObserver)
        }

        binding.viewModel = signInViewModel

        email.afterTextChanged {
            signInViewModel.loginDataChanged(
                email.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                signInViewModel.loginDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        signInViewModel.login(
                            email.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                signInViewModel.login(email.text.toString(), password.text.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        signInViewModel.start()
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLoginFailed(message: Int) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
