package com.hmmelton.releave.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.hmmelton.releave.R

/**
 * This class is a custom [RecyclerView.Adapter] used in [com.hmmelton.releave.views.RestroomForm] to populate a list of
 * nearby places.
 */
class RestroomFormAdapter : RecyclerView.Adapter<RestroomFormAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "RestroomFormAdapter"
    }

    private var places = listOf<Place>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.restroom_form_recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = places.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place)
    }

    fun setItems(buffer: PlaceLikelihoodBufferResponse) {
        places = buffer
            .sortedByDescending { it.likelihood }
            .map { it.place.freeze() }
            .also { buffer.release() }
        notifyDataSetChanged()
    }

    fun clear() {
        places = emptyList()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView = (itemView.findViewById(R.id.tvLabel) as TextView).apply {
            setTextColor(ContextCompat.getColor(itemView.context, R.color.colorTextSecondary))
        }

        fun bind(place: Place) {
            textView.text = place.name

            itemView.setOnClickListener {
                Log.d(TAG, place.name.toString())

                val addressSections = place.address?.split(", ") ?: return@setOnClickListener
                val addressMultiLine =
                    String.format("%s\n %s, %s", addressSections[0], addressSections[1], addressSections[2])
                Log.d(TAG, addressMultiLine)
            }
        }
    }
}
