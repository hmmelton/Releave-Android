package com.releave.android.signin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.releave.android.R
import com.releave.android.databinding.FragmentSignInBinding
import com.releave.android.views.PasswordResetDialog
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_sign_in.*
import javax.inject.Inject

class SignInFragment : Fragment() {

    private lateinit var viewModel: SignInViewModel

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
                    requireContext(),
                    action.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            SignInViewModel.Action.DisplayPasswordResetDialog -> {
                // Display dialog prompting user to enter email for password reset
                PasswordResetDialog().apply {
                    callback = { email -> viewModel.sendResetPasswordEmail(email = email) }
                }.show(requireFragmentManager(), "PasswordResetDialog")
            }
            SignInViewModel.Action.HideSpinner -> loading.visibility = View.GONE
            SignInViewModel.Action.DisplaySpinner -> loading.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders
            .of(this)[SignInViewModel::class.java]
            .apply {
                signInFormState.observe(this@SignInFragment, signInFormStateObserver)
                signInResult.observe(this@SignInFragment, signInResultObserver)
                action.observe(this@SignInFragment, actionObserver)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSignInBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sign_in,
            container,
            false
        )

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email.afterTextChanged {
            viewModel.loginDataChanged(
                email.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                viewModel.loginDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        viewModel.login(email.text.toString(), password.text.toString())
                }
                false
            }

            login.setOnClickListener {
                viewModel.login(email.text.toString(), password.text.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
    }

    private fun showLoginFailed(message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
