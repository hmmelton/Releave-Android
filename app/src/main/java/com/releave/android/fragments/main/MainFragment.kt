package com.releave.android.fragments.main


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

import com.releave.android.R
import com.releave.android.databinding.FragmentMainBinding
import com.releave.android.tools.PermissionsListener
import com.releave.android.tools.PermissionsManager
import kotlinx.android.synthetic.main.fragment_main.*

private const val TAG = "MainFragment"

/**
 * A simple [Fragment] subclass.
 */
class MainFragment(
    private val databaseReference: FirebaseFirestore = FirebaseFirestore.getInstance()
) : Fragment() {

    private val permissionsManager: PermissionsManager by lazy {
        PermissionsManager(permissionsListener)
    }
    private lateinit var viewModel: MainViewModel

    private val permissionsListener = object : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        }

        override fun onPermissionResult(granted: Boolean) {
            Log.e(TAG, "onPermissionResult")
            viewModel.areLocationPermissionsGranted = granted
            if (granted) {
                setUpMap()
            } else {
                Log.e(TAG, "isGranted")
                createLocationPermissionDialog().show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        setUpMap()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()

        // Putting this check here allows the app to update when the user leaves to grant
        // permission from system settings, then returns
        val areLocationPermissionsGranted =
            PermissionsManager.areLocationPermissionsGranted(requireContext())

        viewModel.areLocationPermissionsGranted = areLocationPermissionsGranted

        if (!areLocationPermissionsGranted) {
            //permissionsManager.requestLocationPermissions(requireActivity())
            permissionsManager.requestLocationPermissionsFromFragment(this)
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menuSignOut) {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setUpMap() {
        mapView.getMapAsync {  map ->
            map.setStyle(Style.MAPBOX_STREETS) { style ->
                enableLocationComponent(map, style)
            }
        }
    }

    private fun enableLocationComponent(map: MapboxMap, style: Style) {
        val areLocationPermissionsGranted =
            PermissionsManager.areLocationPermissionsGranted(requireContext())

        viewModel.areLocationPermissionsGranted = areLocationPermissionsGranted

        // If user has granted location permission
        if (areLocationPermissionsGranted) {
            val locationComponent = map.locationComponent
            val activationOptions = LocationComponentActivationOptions
                .builder(requireContext(), style)
                .build()

            locationComponent.apply {
                activateLocationComponent(activationOptions)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
            }
        } else {
            //permissionsManager.requestLocationPermissions(requireActivity())
            permissionsManager.requestLocationPermissionsFromFragment(this)
        }
    }

    private fun createLocationPermissionDialog() = AlertDialog.Builder(requireContext())
        .setTitle(R.string.alert_must_give_location_permission)
        .setPositiveButton(android.R.string.ok) { _, _ ->
            // This Intent takes the user to the app's system settings page
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireActivity().packageName, null)
            }
            startActivity(settingsIntent)
        }
        .setNegativeButton(android.R.string.cancel, null)
        .create()
}
