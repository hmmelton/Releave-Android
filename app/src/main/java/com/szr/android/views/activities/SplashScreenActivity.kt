package com.szr.android.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()

        // If user is logged in, navigate to main screen, else navigate to login
        if (auth.currentUser != null && auth.currentUser?.isEmailVerified == true) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        finish()
    }
}
