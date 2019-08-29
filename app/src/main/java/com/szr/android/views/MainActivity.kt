package com.szr.android.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.szr.android.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, dest, _ ->
            val noToolbarList = listOf(R.id.splashScreenFragment, R.id.signInFragment)
            toolbar.visibility = if (dest.id in noToolbarList) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                findNavController(R.id.nav_host_fragment).navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
