package com.hmmelton.releave.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.releave.main.MainActivity
import com.hmmelton.releave.signin.SignInActivity

class SplashScreenActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<View>(android.R.id.content).systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE

        // Grab most recent signed in account
        FirebaseAuth.getInstance().addAuthStateListener {

            // If an account was found, navigate to MainActivity
            it.currentUser?.let {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            // Otherwise, navigate to SignInActivity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
}
