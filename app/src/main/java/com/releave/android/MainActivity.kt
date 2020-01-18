package com.releave.android

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /**
     * Desination change listener used to change presence of AppBar
     */
    private val navDestinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            appBar.visibility = when (destination.id) {
                R.id.mainFragment -> View.VISIBLE
                else -> View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        findNavController(R.id.nav_host_fragment)
            .addOnDestinationChangedListener(navDestinationChangedListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        findNavController(R.id.nav_host_fragment)
            .removeOnDestinationChangedListener(navDestinationChangedListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
