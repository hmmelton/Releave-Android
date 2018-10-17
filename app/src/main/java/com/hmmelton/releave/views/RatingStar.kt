package com.hmmelton.releave.views

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.widget.ImageView
import com.hmmelton.releave.R

class RatingStar(context: Context) : ImageView(context) {

    private var isHighlighted = false

    init {
        val image = ResourcesCompat.getDrawable(context.resources, R.drawable.star_off, null)
        setImageDrawable(image)
    }

    /**
     * This function highlights or unhighlights the star, depending on the value of the parameter.
     *
     * @param isHighlighted whether or not the star should be highlighted
     */
    fun setHighlighted(isHighlighted: Boolean) {
        val imageRes = if (isHighlighted) R.drawable.star_on else R.drawable.star_off
        val image = ResourcesCompat.getDrawable(context.resources, imageRes, null)
        setImageDrawable(image)

        this.isHighlighted = isHighlighted
    }
}
