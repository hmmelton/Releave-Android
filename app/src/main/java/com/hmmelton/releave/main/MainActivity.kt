package com.hmmelton.releave.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.hmmelton.releave.R
import com.hmmelton.releave.helpers.BaseActivity
import com.hmmelton.releave.signin.SignInActivity
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(), OnMapReadyCallback {

    companion object {

        // Both these values are in seconds
        private const val LOCATION_REQUEST_INTERVAL = 5L
        private const val LOCATION_REQUEST_FASTEST_INTERVAL = 1L

        private const val MAP_ZOOM_LEVEL = 16f
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get reference to map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
}
