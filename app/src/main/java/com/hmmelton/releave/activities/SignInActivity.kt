package com.hmmelton.releave.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.hmmelton.releave.R
import com.hmmelton.releave.data.TokenRepository
import com.hmmelton.releave.data.UserRepository
import com.hmmelton.releave.data.models.AuthRequestBody
import com.hmmelton.releave.data.models.User
import com.hmmelton.releave.helpers.BaseActivity
import com.hmmelton.releave.services.ReleaveClient
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : BaseActivity() {

    companion object {
        private const val EMAIL_PERMISSION = "email"
    }

    private val callbackManager = CallbackManager.Factory.create()

    private val facebookCallback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult?) {
            result?.accessToken?.let { accessToken -> fetchUserInfo(accessToken = accessToken) }
        }

        override fun onCancel() {
            hideProgressBar()
        }

        override fun onError(error: FacebookException) {
            hideProgressBar()
            displayErrorDialog(title = getString(R.string.err_title_sign_in_failed), exception = error)
        }
    }

    private val authCallback = object : Callback<User> {
        override fun onFailure(call: Call<User>, t: Throwable) {
            Log.e("SignInActivity", "Error -> ${t.localizedMessage}")
            alertSignInFailed()
            LoginManager.getInstance().logOut()
        }

        override fun onResponse(call: Call<User>, response: Response<User>) {
            if (!response.isSuccessful) {
                alertSignInFailed()
                return
            }

            val token = response.headers().get("Authorization")
            val user = response.body()

            if (token == null || user == null) {
                alertSignInFailed()
                LoginManager.getInstance().logOut()
            } else {
                signInUser(user = user, token = token)
            }
        }
    }

    private val graphRequestCallback = GraphRequest.GraphJSONObjectCallback { jsonObject, _ ->
        val id = jsonObject.optString("id", "")
        val firstName = jsonObject.optString("first_name", "")
        val lastName = jsonObject.optString("last_name", "")
        val email = jsonObject.optString("email", "")

        // If email is not present, alert user and log out
        if (email.isEmpty()) {
            displayErrorDialog(
                title = getString(R.string.err_title_email_missing),
                message = getString(R.string.err_message_email_missing)
            )
            LoginManager.getInstance().logOut()
        } else if (id.isEmpty() || firstName.isEmpty()) {
            displayErrorDialog(
                title = getString(R.string.err_title_sign_in_failed),
                message = getString(R.string.err_message_missing_user_info)
            )
        } else {

            // Email was returned - attempt to authenticate user
            val requestBody = AuthRequestBody(
                facebookId = id,
                firstName = firstName,
                lastName = lastName,
                email = email
            )

            ReleaveClient.service.authenticate(
                facebookAuthToken = AccessToken.getCurrentAccessToken().token,
                body = requestBody
            ).enqueue(authCallback)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setUpFacebookLogin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        showProgressBar()
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setUpFacebookLogin() {
        signInButtonFacebook.setReadPermissions(listOf(EMAIL_PERMISSION))

        signInButtonFacebook.registerCallback(callbackManager, facebookCallback)
    }

    /**
     * This function fetches the user's information (email, name, etc).
     *
     * @param accessToken Facebook API access token used to fetch user info
     */
    private fun fetchUserInfo(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken, graphRequestCallback)

        val bundle = Bundle()
        bundle.putString("fields", "email,first_name,last_name")
        request.parameters = bundle
        request.executeAsync()
    }

    private fun alertSignInFailed() {
        displayErrorDialog(
            title = getString(R.string.err_title_sign_in_failed),
            message = getString(R.string.err_message_sign_in_failed)
        )
    }

    private fun signInUser(user: User, token: String) {

        // Save user and token data
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        TokenRepository.setToken(token = token, preferences = preferences)
        UserRepository.setCurrentUser(user = user, preferences = preferences)

        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun showProgressBar() {
        signInButtonFacebook.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        signInButtonFacebook.visibility = View.VISIBLE
    }
}
