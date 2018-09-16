package com.hmmelton.releave.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import com.hmmelton.releave.R

open class BaseActivity : AppCompatActivity() {

    companion object {
        const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        const val REQUEST_CODE_LOCATION = 1001
        private const val LOCATION_PERMISSION_KEY = "com.hmmelton.releave.keys.location_permission"
    }

    private var isRequestingPermission = false

    private var hasRequestedLocationPermission
        @SuppressLint("ApplySharedPref")
        set(value) {
            PreferenceManager
                .getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(LOCATION_PERMISSION_KEY, value)
                .commit()
        }
        get() = PreferenceManager
            .getDefaultSharedPreferences(this)
            .getBoolean(LOCATION_PERMISSION_KEY, false)

    /**
     * Intent used to take user to app settings
     */
    private val settingsIntent: Intent by lazy {
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
    }

    /**
     * [Snackbar] that requests location permission from user
     */
    private val locationPermissionSnackbar: Snackbar by lazy {
        val view = findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(view, R.string.alert_location_permission_title, Snackbar.LENGTH_INDEFINITE)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION) &&
            hasRequestedLocationPermission) {

            // User has asked not to be prompted for location permissions again
            snackbar
                .setAction(R.string.btn_text_allow, {
                    startActivity(settingsIntent)
                })
        } else {
            snackbar
                .setAction(R.string.btn_text_allow, {
                    requestLocationPermission()
                })
        }

        snackbar
    }

    override fun onStart() {
        super.onStart()

        checkForLocationPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isRequestingPermission = false

        if (requestCode != REQUEST_CODE_LOCATION || grantResults.isEmpty()) return

        hasRequestedLocationPermission = true

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            // Permission was granted, hide Snackbar
            alertNeedsLocationPermission()
        }
    }

    /**
     * This function requests the location permission, if it has not been granted.
     */
    private fun checkForLocationPermission() {
        if (hasPermission(LOCATION_PERMISSION)) return

        requestLocationPermission()
    }

    /**
     * This function displays a dialog requesting the user turn on the location permission from app settings.
     */
    private fun displayLocationPermissionDialog() {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
        } else {
            AlertDialog.Builder(this)
        }

        val dialog = builder
            .setTitle(R.string.alert_location_permission_title)
            .setMessage(R.string.alert_location_permission_message)
            .setPositiveButton(R.string.btn_text_allow) { _, _ ->
                startActivity(settingsIntent)
            }
            .setNegativeButton(android.R.string.cancel, { _, _ ->
                alertNeedsLocationPermission()
            })
            .create()

        // Remove extra space at top of dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
    }

    /**
     * This function checks if the given permission has been granted.
     *
     * @param permission the permission we are checking
     * @return whether or not the permission has been granted
     */
    protected fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    protected fun requestLocationPermission() {
        if (isRequestingPermission) return

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION) &&
            hasRequestedLocationPermission) {

            // The user has requested not to be asked for this permission again
            displayLocationPermissionDialog()
        } else {

            // This is any other condition...
            isRequestingPermission = true
            if (!locationPermissionSnackbar.isShown) locationPermissionSnackbar.dismiss()
            ActivityCompat.requestPermissions(this, arrayOf(LOCATION_PERMISSION), REQUEST_CODE_LOCATION)
        }
    }

    /**
     * This function displays the location permission [Snackbar].
     */
    private fun alertNeedsLocationPermission() {
        if (isRequestingPermission) return
        if (!locationPermissionSnackbar.isShown) locationPermissionSnackbar.show()
    }
}
