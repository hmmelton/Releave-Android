package com.releave.android.tools

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import java.util.ArrayList

/**
 * Helps request permissions at runtime.
 */
class PermissionsManager(var listener: PermissionsListener?) {

    private val REQUEST_PERMISSIONS_CODE = 0

    fun requestLocationPermissionsFromActivity(activity: Activity) {
        // Request fine location permissions by default
        requestLocationPermissionsFromActivity(activity, true)
    }

    private fun requestLocationPermissionsFromActivity(activity: Activity, requestFineLocation: Boolean) {
        val permissions = if (requestFineLocation)
            arrayOf(FINE_LOCATION_PERMISSION)
        else
            arrayOf(COARSE_LOCATION_PERMISSION)
        requestPermissionsFromActivity(activity, permissions)
    }

    private fun requestPermissionsFromActivity(activity: Activity, permissions: Array<String>) {
        val permissionsToExplain = ArrayList<String>()
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                permissionsToExplain.add(permission)
            }
        }

        if (permissionsToExplain.isNotEmpty()) {
            // The developer should show an explanation to the user asynchronously
            listener?.onExplanationNeeded(permissionsToExplain)
        }

        ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSIONS_CODE)
    }

    fun requestLocationPermissionsFromFragment(fragment: Fragment) {
        // Request fine location permissions by default
        requestLocationPermissionsFromFragment(fragment, true)
    }

    private fun requestLocationPermissionsFromFragment(
        fragment: Fragment,
        requestFineLocation: Boolean
    ) {
        val permissions = if (requestFineLocation)
            arrayOf(FINE_LOCATION_PERMISSION)
        else
            arrayOf(COARSE_LOCATION_PERMISSION)
        requestPermissionsFromActivityFragment(fragment, permissions)
    }

    private fun requestPermissionsFromActivityFragment(fragment: Fragment, permissions: Array<String>) {
        val permissionsToExplain = ArrayList<String>()
        for (permission in permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                permissionsToExplain.add(permission)
            }
        }

        if (permissionsToExplain.isNotEmpty()) {
            // The developer should show an explanation to the user asynchronously
            listener?.onExplanationNeeded(permissionsToExplain)
        }

        fragment.requestPermissions(permissions, REQUEST_PERMISSIONS_CODE)
    }

    /**
     * You should call this method from your activity onRequestPermissionsResult.
     *
     * @param requestCode The request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either
     * PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                val granted =
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                listener?.onPermissionResult(granted)
            }
        }
    }

    companion object {

        private const val COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

        private fun isPermissionGranted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        private fun isCoarseLocationPermissionGranted(context: Context): Boolean {
            return isPermissionGranted(context, COARSE_LOCATION_PERMISSION)
        }

        private fun isFineLocationPermissionGranted(context: Context): Boolean {
            return isPermissionGranted(context, FINE_LOCATION_PERMISSION)
        }

        fun areLocationPermissionsGranted(context: Context): Boolean {
            return isCoarseLocationPermissionGranted(context) ||
                    isFineLocationPermissionGranted(context)
        }

        fun areRuntimePermissionsRequired() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }
}
