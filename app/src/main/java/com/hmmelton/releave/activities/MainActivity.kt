package com.hmmelton.releave.activities

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.facebook.login.LoginManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.hmmelton.releave.R
import com.hmmelton.releave.data.TokenRepository
import com.hmmelton.releave.data.UserRepository
import com.hmmelton.releave.dialogs.RestroomFormDialog
import com.hmmelton.releave.helpers.BaseActivity
import com.hmmelton.releave.views.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(), OnMapReadyCallback {

    companion object {

        // Both these values are in seconds
        private const val LOCATION_REQUEST_INTERVAL = 5L
        private const val LOCATION_REQUEST_FASTEST_INTERVAL = 1L

        private const val MAP_ZOOM_LEVEL = 16f
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var viewModel: MainActivityViewModel

    private var map: GoogleMap? = null

    private val placeDetectionClient
        get() = Places.getPlaceDetectionClient(this)

    /**
     * Callback for device location updates
     */
    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {
            val result = locationResult ?: return

            val locationList = result.locations
            if (locationList.isNotEmpty()) {

                // Update last known location
                val lastLocation = locationList.last()

                val location = lastLocation ?: return
                val latLng = LatLng(location.latitude, location.longitude)

                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL))
            }
        }
    }

    private val fabOnClickListener = View.OnClickListener { displayFormDialog() }
    private val restroomUploadErrorHandler = {
        Snackbar
            .make(findViewById(android.R.id.content), R.string.snack_error_uploading_restroom, Snackbar.LENGTH_LONG)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setActionBar()

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java).apply {
            errorHandler = restroomUploadErrorHandler
        }

        // Get reference to map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        addRestroomFab.setOnClickListener(fabOnClickListener)
    }

    override fun onStart() {
        super.onStart()
        updateLocationUi()
        displayCurrentLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                // Log out
                val preferences = PreferenceManager.getDefaultSharedPreferences(this)
                UserRepository.setCurrentUser(user = null, preferences = preferences)
                TokenRepository.setToken(token = null, preferences = preferences)
                LoginManager.getInstance().logOut()

                // Return to sign in page
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            else -> throw IllegalArgumentException("Unrecognized item: ${item.itemId}")
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_LOCATION) return

        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            // Permission was granted, start tracking location
            displayCurrentLocation()
        }

        updateLocationUi()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        // Update the map to show (or not) the "my location" layer
        updateLocationUi()

        // Get user's current location and display it on the map
        displayCurrentLocation()
    }

    private fun setActionBar() {
        setSupportActionBar(mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun updateLocationUi() {
        if (map == null) return

        try {
            if (hasPermission(LOCATION_PERMISSION)) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
            }
            map?.uiSettings?.isZoomGesturesEnabled = false
            map?.uiSettings?.isZoomControlsEnabled = false
        } catch (e: SecurityException) {
            requestLocationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayCurrentLocation() {

        if (hasPermission(LOCATION_PERMISSION)) {
            // Prepare location request
            val locationRequest = LocationRequest()
            locationRequest.interval = TimeUnit.SECONDS.toMillis(LOCATION_REQUEST_INTERVAL)
            locationRequest.fastestInterval = TimeUnit.SECONDS.toMillis(LOCATION_REQUEST_FASTEST_INTERVAL)
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        } else {
            requestLocationPermission()
        }
    }

    private fun displayFormDialog() {
        val transaction = supportFragmentManager.beginTransaction()
        supportFragmentManager.findFragmentByTag("dialog")?.let { previous -> transaction.remove(previous) }
        transaction.addToBackStack(null)

        try {
            val placeResult = placeDetectionClient.getCurrentPlace(null)
            placeResult.addOnCompleteListener { task ->

                task.result?.let { result ->
                    RestroomFormDialog.newInstance(result).apply {
                        saveRestroomCallback = viewModel
                        show(transaction, "dialog")
                    }

                    return@addOnCompleteListener
                }

                Snackbar
                    .make(findViewById(android.R.id.content), R.string.snack_no_nearby_locations, Snackbar.LENGTH_LONG)
                    .show()
            }
        } catch (e: SecurityException) {
            requestLocationPermission()
        }
    }
}
