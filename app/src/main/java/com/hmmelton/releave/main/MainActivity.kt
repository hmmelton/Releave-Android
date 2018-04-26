package com.hmmelton.releave.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.releave.R
import com.hmmelton.releave.signin.SignInActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                // Log out of Firebase
                FirebaseAuth.getInstance().signOut()

                // Return to sign in page
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            else -> throw IllegalArgumentException("Unrecognized item: ${item.itemId}")
        }

        return super.onOptionsItemSelected(item)
    }
}
