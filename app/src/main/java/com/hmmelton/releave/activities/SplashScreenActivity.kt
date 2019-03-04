package com.hmmelton.releave.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hmmelton.releave.data.UserRepository

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

        // If there is no current user, navigate to sign in
        if (UserRepository.getCurrentUser(PreferenceManager.getDefaultSharedPreferences(this)) == null) {
            startActivity(Intent(this, SignInActivity::class.java))
        } else {

            // Otherwise, navigate to main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
