package com.hmmelton.releave.signin

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hmmelton.releave.R
import com.hmmelton.releave.main.MainActivity
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 123
    }

    private lateinit var gso: GoogleSignInOptions

    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set OnClickListener for sign in button
        signInButtonGoogle.setOnClickListener(signInButtonOnClickListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
                ?: throw Exception()
            firebaseAuthWithGoogleAccount(account)
        } catch (e: Exception) {

            // Hide cover and notify user of failure
            signInCover.visibility = View.GONE
            Snackbar.make(
                findViewById<View>(android.R.id.content),
                getString(R.string.err_sign_in_failed),
                Snackbar.LENGTH_LONG
            ).show()

            e.printStackTrace()
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credentials).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                finish()
            } else {

                // Hide cover and notify user of error
                signInCover.visibility = View.GONE
                Snackbar.make(
                    findViewById<View>(android.R.id.content),
                    getString(R.string.err_sign_in_failed),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    /** [View.OnClickListener] for Google sign in button */
    private val signInButtonOnClickListener = View.OnClickListener {
        signInCover.visibility = View.VISIBLE
        startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE_SIGN_IN)
    }
}
