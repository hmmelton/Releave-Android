package com.hmmelton.releave.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.hmmelton.releave.R

/**
 * This custom [View] serves as a replica & replacements of [android.widget.RatingBar].
 *
 * The only custom attribute provided is `numStars`, which defines the number of stars to display.
 */
class RatingWidget(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val numStars: Int
    private var selectedRating: Int

    init {
        inflate(context, R.layout.view_rating_widget, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RatingWidget)
        numStars = attributes.getInt(R.styleable.RatingWidget_numStars, 5)
        attributes.recycle()

        selectedRating = 0

        addStarsToView(numStars = numStars)
    }

    private fun addStarsToView(numStars: Int) {
        val content = findViewById<LinearLayout>(R.id.ratingWidgetLayout)
        content.weightSum = numStars.toFloat()

        // Add stars to widget
        for (i in 0 until numStars) {
            content.addView(createRatingStar(index = i))
        }
    }

    private fun createRatingStar(index: Int): RatingStar {
        val view = RatingStar(context)

        // The width is set to 0 with a weight, so all stars take an equal width in the parent view
        view.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            weight = 1f
        }
        view.isClickable = true
        view.setOnClickListener { onRatingSelected(index) }

        return view
    }

    private fun onRatingSelected(index: Int) {
        val content = findViewById<LinearLayout>(R.id.ratingWidgetLayout)

        // Highlight views up to and including the one at the selected index
        for (i in 0..index) {
            (content.getChildAt(i) as RatingStar).setHighlighted(true)
        }
        if (index + 1 == content.childCount) return

        // Unhighlight all views after the one at the selected index
        for (i in (index + 1) until content.childCount) {
            (content.getChildAt(i) as RatingStar).setHighlighted(false)
        }

        selectedRating = index + 1
    }
}
