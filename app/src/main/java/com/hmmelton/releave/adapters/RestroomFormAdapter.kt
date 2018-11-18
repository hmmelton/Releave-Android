package com.hmmelton.releave.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.gms.location.places.Place
import com.hmmelton.releave.R

/**
 * This class is a custom adapter used in [com.hmmelton.releave.dialogs.RestroomFormDialog] to populate a list of
 * nearby places.
 */
class RestroomFormSpinnerAdapter(
    private val ctx: Context,
    private val places: Array<Place>
) : ArrayAdapter<CharSequence>(ctx, R.layout.spinner_item, places.map { it.name }) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val listItem = convertView as? TextView
            ?: LayoutInflater.from(ctx).inflate(R.layout.spinner_item, parent, false) as TextView

        val currentPlace = places[position]
        listItem.text = currentPlace.name

        return listItem
    }
}
