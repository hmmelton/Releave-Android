package com.hmmelton.releave.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.releave.main.MainActivity
import com.hmmelton.releave.signin.SignInActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<View>(android.R.id.content).systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE

        val currentUser = FirebaseAuth.getInstance().currentUser

        // If an account was found, navigate to MainActivity
        if (currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        } else {

            // Otherwise, navigate to SignInActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
