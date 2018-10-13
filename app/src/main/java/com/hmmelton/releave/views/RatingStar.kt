package com.hmmelton.releave.views

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.widget.ImageView
import com.hmmelton.releave.R

class RatingStar(context: Context) : ImageView(context) {

    private var isHighlighted = false

    init {
        val image = ResourcesCompat.getDrawable(context.resources, android.R.drawable.star_off, null)?.apply {
            val color = ContextCompat.getColor(context, R.color.colorStarOff)
            setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
        setImageDrawable(image)
    }

    /**
     * This function highlights or unhighlights the star, depending on the value of the parameter.
     *
     * @param isHighlighted whether or not the star should be highlighted
     */
    fun setHighlighted(isHighlighted: Boolean) {
        if (this.isHighlighted == isHighlighted) return

        val colorRes = if (isHighlighted) R.color.colorStarOn else R.color.colorStarOff
        val image = ResourcesCompat.getDrawable(context.resources, android.R.drawable.star_off, null)?.apply {
            val color = ContextCompat.getColor(context, colorRes)
            setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
        setImageDrawable(image)

        this.isHighlighted = isHighlighted
    }
}
