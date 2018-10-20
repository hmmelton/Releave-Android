package com.hmmelton.releave.views

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.LinearLayout
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.google.android.gms.location.places.Places
import com.hmmelton.releave.R
import com.hmmelton.releave.adapters.RestroomFormAdapter
import kotlinx.android.synthetic.main.view_restroom_form.view.*

class RestroomForm(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val TAG = "RestroomForm"

    private val placeDetectionClient = Places.getPlaceDetectionClient(context)
    private val adapter = RestroomFormAdapter()

    init {
        inflate(context, R.layout.view_restroom_form, this)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(context, VERTICAL)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    @SuppressLint("MissingPermission")
    private fun getLikelyPlaces(resultHandler: (PlaceLikelihoodBufferResponse) -> Unit) {
        val placeResult = placeDetectionClient.getCurrentPlace(null)
        placeResult.addOnCompleteListener {
            val likelyPlaces = it.result ?: return@addOnCompleteListener
            for (place in likelyPlaces) {
                Log.d("RestroomForm", "${place.place.name}")
            }
            resultHandler(likelyPlaces)
        }
    }

    /**
     * This function creates an animation to shrink the "add restroom" layout.
     *
     * @param content [View] that this form will shrink from
     * @param duration animation duration in milliseconds
     * @param corner corner of the screen to which the animation will shrink
     *
     * @return [Animator] for animating the layout
     */
    fun createShrinkLayoutAnimation(content: View, duration: Long, corner: Corner): Animator {
        val x: Int
        val y: Int

        when (corner) {
            Corner.BOTTOM_RIGHT -> {
                x = this.right
                y = this.bottom
            }
            Corner.BOTTOM_LEFT -> {
                x = this.left
                y = this.bottom
            }
            Corner.TOP_LEFT -> {
                x = this.left
                y = this.top
            }
            Corner.TOP_RIGHT -> {
                x = this.right
                y = this.top
            }
        }

        val startRadius = Math.hypot(content.width.toDouble(), content.height.toDouble()).toInt()
        val endRadius = 0

        val anim =
            ViewAnimationUtils.createCircularReveal(this, x, y, startRadius.toFloat(), endRadius.toFloat())

        anim.duration = duration

        return anim
    }

    /**
     * This function creates an animation to expand the "add restroom" layout.
     *
     * @param content [View] that this form will expand to fill
     * @param duration animation duration in milliseconds
     * @param corner corner of the screen from which the animation will expand
     *
     * @return [Animator] for animating the layout
     */
    fun createExpandLayoutAnimation(content: View, duration: Long, corner: Corner): Animator {
        val x: Int
        val y: Int

        when (corner) {
            Corner.BOTTOM_RIGHT -> {
                x = content.right
                y = content.bottom
            }
            Corner.BOTTOM_LEFT -> {
                x = content.left
                y = content.bottom
            }
            Corner.TOP_LEFT -> {
                x = content.left
                y = content.top
            }
            Corner.TOP_RIGHT -> {
                x = content.right
                y = content.top
            }
        }

        val startRadius = 0
        val endRadius = Math.hypot(content.width.toDouble(), content.height.toDouble()).toInt()

        val anim =
            ViewAnimationUtils.createCircularReveal(this, x, y, startRadius.toFloat(), endRadius.toFloat())

        anim.addListener(expandAnimationListener)

        anim.duration = duration

        return anim
    }

    /**
     * This [Animator.AnimatorListener] handles adding/removing items from the RecyclerView's adapter.
     */
    private val expandAnimationListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            adapter.clear()
            progressBar.visibility = View.VISIBLE
        }

        override fun onAnimationEnd(animation: Animator?) {
            getLikelyPlaces {
                progressBar.visibility = View.GONE

                adapter.setItems(buffer = it)
            }
        }

        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }
    }

    /**
     * This class is used to reference a corner of a [View].
     */
    enum class Corner {
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        TOP_LEFT,
        TOP_RIGHT
    }
}
