package com.hmmelton.releave.main

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_add_restroom.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(), OnMapReadyCallback {

    companion object {

        // Both these values are in seconds
        private const val LOCATION_REQUEST_INTERVAL = 5L
        private const val LOCATION_REQUEST_FASTEST_INTERVAL = 1L

        private const val MAP_ZOOM_LEVEL = 16f
        private const val ANIMATION_DURATION = 200L
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var map: GoogleMap? = null
    private var isAddRestroomLayoutVisible = false

    /**
     * This listener is used for [android.support.design.widget.FloatingActionButton] expand and shrink animations.
     */
    private val animationListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (isAddRestroomLayoutVisible) {
                addRestroomView.visibility = View.GONE
            }
            isAddRestroomLayoutVisible = !isAddRestroomLayoutVisible
            setActionBar()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            if (!isAddRestroomLayoutVisible) {
                addRestroomView.visibility = View.VISIBLE
            }
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

    private val fabOnClickListener = View.OnClickListener { animateDisplayAddRestroomLayout() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setActionBar()

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
        if (!isAddRestroomLayoutVisible) menuInflater.inflate(R.menu.menu_activity_main, menu)
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
            android.R.id.home -> {
                animateHideAddRestroomLayout()
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
        if (isAddRestroomLayoutVisible) {
            setSupportActionBar(addRestroomToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        } else {
            setSupportActionBar(mainToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }
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
     * This function animates the flow displaying the "add new restroom" layout.
     */
    private fun animateDisplayAddRestroomLayout() {
        val diagonalOutAnimation = createDiagonalOutMotionAnimation()
        val expandLayoutAnimation = createExpandLayoutAnimation()

        val set = AnimatorSet()
        set.playSequentially(diagonalOutAnimation, expandLayoutAnimation)
        set.start()
    }

    /**
     * This function animates the flow hiding the "add new restroom" layout.
     */
    private fun animateHideAddRestroomLayout() {
        val shrinkLayoutAnimation = createShrinkLayoutAnimation()
        val diagonalInAnimation = createDiagonalInMotionAnimation()

        val set = AnimatorSet()
        set.playSequentially(shrinkLayoutAnimation, diagonalInAnimation)
        set.start()
    }

    /**
     * This function creates an animation to expand the "add restroom" layout.
     *
     * @return [Animator] for animating the layout
     */
    private fun createExpandLayoutAnimation(): Animator {
        val mainView = findViewById<View>(android.R.id.content)

        val x = mainView.right
        val y = mainView.bottom
        val startRadius = 0
        val endRadius = Math.hypot(mainView.width.toDouble(), mainView.height.toDouble()).toInt()

        val anim =
            ViewAnimationUtils.createCircularReveal(addRestroomView, x, y, startRadius.toFloat(), endRadius.toFloat())

        anim.addListener(animationListener)
        anim.duration = ANIMATION_DURATION

        return anim
    }

    /**
     * This function creates an animation to shrink the "add restroom" layout.
     *
     * @return [Animator] for animating the layout
     */
    private fun createShrinkLayoutAnimation(): Animator {
        val mainView = findViewById<View>(android.R.id.content)

        val x = addRestroomView.right
        val y = addRestroomView.bottom

        val startRadius = Math.hypot(mainView.width.toDouble(), mainView.height.toDouble()).toInt()
        val endRadius = 0

        val anim =
            ViewAnimationUtils.createCircularReveal(addRestroomView, x, y, startRadius.toFloat(), endRadius.toFloat())

        anim.addListener(animationListener)
        anim.duration = ANIMATION_DURATION

        return anim
    }

    /**
     * This function creates an [AnimatorSet] for animating the FAB off screen.
     *
     * @return [AnimatorSet] used for FAB animation
     */
    private fun createDiagonalOutMotionAnimation(): AnimatorSet {
        val layoutParams = addRestroomFab.layoutParams as ConstraintLayout.LayoutParams

        val xTranslationDistance = (layoutParams.marginEnd + (addRestroomFab.width / 2)).toFloat()
        val xAnimation = ObjectAnimator.ofFloat(addRestroomFab, "translationX", xTranslationDistance)
        xAnimation.interpolator = AccelerateInterpolator()

        val yTranslationDistance = (layoutParams.bottomMargin + (addRestroomFab.height / 2)).toFloat()
        val yAnimation = ObjectAnimator.ofFloat(addRestroomFab, "translationY", yTranslationDistance)
        yAnimation.interpolator = DecelerateInterpolator()

        val set = AnimatorSet()
        set.playTogether(xAnimation, yAnimation)
        set.duration = ANIMATION_DURATION

        return set
    }

    /**
     * This function creates an [AnimatorSet] for animating the FAB onto the screen.
     *
     * @return [AnimatorSet] used for FAB animation
     */
    private fun createDiagonalInMotionAnimation(): AnimatorSet {
        val xTranslationDistance = 0f
        val xAnimation = ObjectAnimator.ofFloat(addRestroomFab, "translationX", xTranslationDistance)
        xAnimation.interpolator = DecelerateInterpolator()

        val yTranslationDistance = 0f
        val yAnimation = ObjectAnimator.ofFloat(addRestroomFab, "translationY", yTranslationDistance)
        yAnimation.interpolator = AccelerateInterpolator()

        val set = AnimatorSet()
        set.playTogether(xAnimation, yAnimation)
        set.duration = ANIMATION_DURATION

        return set
    }
}
