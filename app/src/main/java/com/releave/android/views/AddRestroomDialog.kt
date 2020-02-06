package com.releave.android.views

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.libraries.places.api.model.Place
import com.releave.android.R

class AddRestroomDialog(private val places: List<Place>) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater

        val view = inflater.inflate(R.layout.layout_add_restroom, null)
        setUpSpinner(view)

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton(R.string.btn_submit) { dialog, _ ->
                // TODO: send selection info back to listener
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setTitle(R.string.label_place)
            .create()
    }

    private fun setUpSpinner(view: View) {
        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            places.map { it.name }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        view.findViewById<Spinner>(R.id.nearbyPlaceSpinner).adapter = adapter
    }
}