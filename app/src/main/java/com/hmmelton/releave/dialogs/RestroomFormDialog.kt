package com.hmmelton.releave.dialogs

import android.app.Dialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatSpinner
import android.view.View
import android.widget.AdapterView
import android.widget.CheckBox
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.hmmelton.releave.R
import com.hmmelton.releave.adapters.RestroomFormSpinnerAdapter
import com.hmmelton.releave.data.SaveRestroomCallback
import com.hmmelton.releave.data.UserRepository
import com.hmmelton.releave.data.models.RestroomRequestBody
import com.hmmelton.releave.views.RatingWidget

class RestroomFormDialog : DialogFragment() {

    private var places = emptyArray<Place>()
    private var selectedItemIndex = 0

    var saveRestroomCallback: SaveRestroomCallback? = null

    private val spinnerItemClickListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            selectedItemIndex = 0
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedItemIndex = position
        }
    }

    companion object {
        fun newInstance(bufferResponse: PlaceLikelihoodBufferResponse): RestroomFormDialog {
            val dialog = RestroomFormDialog()
            dialog.setPlacesList(bufferResponse)

            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater

        val view = inflater.inflate(R.layout.layout_restroom_form_dialog, null)
        val spinner: AppCompatSpinner = view.findViewById(R.id.spinner)
        val lockedCheckBox: CheckBox = view.findViewById(R.id.lockedCheckBox)
        val singleOccupancyCheckBox: CheckBox = view.findViewById(R.id.singleOccupancyCheckBox)
        val ratingWidget: RatingWidget = view.findViewById(R.id.ratingWidget)

        // Add adapter with data to Spinner
        val adapter = RestroomFormSpinnerAdapter(ctx = requireContext(), places = places)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = spinnerItemClickListener

        return AlertDialog.Builder(requireActivity())
            .setView(view)
            .setTitle("Add Restroom")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                saveNewRestroom(
                    place = places[selectedItemIndex],
                    isLocked = lockedCheckBox.isChecked,
                    isSingleOccupancy = singleOccupancyCheckBox.isChecked,
                    rating = ratingWidget.selectedRating
                )
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> dismiss() }
            .create()
    }

    private fun setPlacesList(bufferResponse: PlaceLikelihoodBufferResponse) {
        places = bufferResponse
            .sortedBy { it.likelihood }
            .map { it.place.freeze() }
            .filterNot { it.address == null }
            .toTypedArray()
        bufferResponse.release()
    }

    /**
     * This function saves a new restroom to storage.
     */
    private fun saveNewRestroom(place: Place, isLocked: Boolean, isSingleOccupancy: Boolean, rating: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        // We just return here, because our auth token interceptor should handle the case in which the user is not
        // authenticated
        val currentUser = UserRepository.getCurrentUser(preferences) ?: return

        val address = place.address ?: return
        val latLng = place.latLng

        val requestBody = RestroomRequestBody(
            createdBy = currentUser.id,
            lat = latLng.latitude,
            lng = latLng.longitude,
            name = place.name.toString(),
            location = address.toString(),
            isLocked = isLocked,
            isSingleOccupancy = isSingleOccupancy,
            rating = rating
        )

        saveRestroomCallback?.onSaveRestroom(requestBody = requestBody)

        dismiss()
    }
}
