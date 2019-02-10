package com.hmmelton.releave.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatSpinner
import android.view.View
import android.widget.AdapterView
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.hmmelton.releave.R
import com.hmmelton.releave.adapters.RestroomFormSpinnerAdapter
import com.hmmelton.releave.models.Restroom
import org.threeten.bp.Instant
import java.util.UUID

class RestroomFormDialog : DialogFragment() {

    private var places = emptyArray<Place>()
    private lateinit var spinner: AppCompatSpinner
    private var selectedItemIndex = 0

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
        val builder = AlertDialog.Builder(requireActivity())
            .setTitle("Add Restroom")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                saveNewRestroom(
                    place = places[selectedItemIndex],
                    isLocked = true,
                    rating = 5.0
                )
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> dismiss() }

        val inflater = requireActivity().layoutInflater

        val view = inflater.inflate(R.layout.layout_restroom_form_dialog, null)
        spinner = view.findViewById(R.id.spinner)

        // Add adapter with data to Spinner
        val adapter = RestroomFormSpinnerAdapter(ctx = requireContext(), places = places)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = spinnerItemClickListener

        builder.setView(view)
        return builder.create()
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
    private fun saveNewRestroom(place: Place, isLocked: Boolean, rating: Double) {
        // TODO: save new restroom to database(s)

        val address = place.address ?: return
        val latLng = place.latLng
        val entry = Restroom(
            id = UUID.randomUUID().toString(),
            name = place.name.toString(),
            location = address.toString(),
            lat = latLng.latitude,
            lng = latLng.longitude,
            isLocked = isLocked,
            rating = rating,
            numRatings = 1,
            createdBy = /* TODO: get user's ID */ "",
            createdWhen = Instant.now(),
            updatedBy = null,
            updatedWhen = null
        )
    }
}
