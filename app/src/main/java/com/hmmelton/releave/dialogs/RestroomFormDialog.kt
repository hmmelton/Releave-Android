package com.hmmelton.releave.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatSpinner
import android.widget.ArrayAdapter
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.hmmelton.releave.R

class RestroomFormDialog : DialogFragment() {

    private var places = emptyList<CharSequence>()
    private lateinit var spinner: AppCompatSpinner

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
                // TODO: add restroom to database
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> dismiss() }

        val inflater = requireActivity().layoutInflater

        val view = inflater.inflate(R.layout.layout_restroom_form_dialog, null)
        spinner = view.findViewById(R.id.spinner)
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, places)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        builder.setView(view)
        return builder.create()
    }

    private fun setPlacesList(bufferResponse: PlaceLikelihoodBufferResponse) {
        places = bufferResponse.sortedBy { it.likelihood }.map { it.place.freeze().name }
    }
}
