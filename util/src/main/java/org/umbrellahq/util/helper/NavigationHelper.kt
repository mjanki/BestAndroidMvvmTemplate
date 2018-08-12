package org.umbrellahq.util.helper

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import org.umbrellahq.util.NavigationUtil
import org.umbrellahq.util.R

fun createSceneTransitionAnimation(activity: FragmentActivity,
                                   sharedView: View? = null,
                                   sharedViews: ArrayList<View>? = null): ActivityOptionsCompat? {

    try {
        if (sharedView != null) {
            val transitionName = ViewCompat.getTransitionName(sharedView) ?: throw IllegalArgumentException()
            return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, sharedView, transitionName)
        } else if (sharedViews != null) {
            val sharedViewsArray = sharedViews.map { view -> Pair.create(view, ViewCompat.getTransitionName(view)) }.toTypedArray()
            return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *sharedViewsArray)
        }
    } catch (e: IllegalArgumentException) {
        Log.e(NavigationUtil.TAG, "Please define transitionName for each of your shared elements")
    }

    return null
}

fun addOverlay(activity: FragmentActivity) {
    if (NavigationUtil.constraintLayoutResId == -1) {
        Log.e(NavigationUtil.TAG, "Setup Constraint Layout Res Id for blocking effect")
        return
    }

    // Remove overlay if exists before adding again
    removeOverlay(activity)

    // Add Overlay
    val constraintLayout = activity.findViewById<ConstraintLayout>(NavigationUtil.constraintLayoutResId)

    val bOverlay = Button(activity)
    bOverlay.id = R.id.overlay_id
    bOverlay.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent))
    bOverlay.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)

    constraintLayout.addView(bOverlay)

    val constraintSet = ConstraintSet()
    constraintSet.clone(constraintLayout)
    constraintSet.connect(bOverlay.id, ConstraintSet.TOP, constraintLayout.id, ConstraintSet.TOP, 0)
    constraintSet.connect(bOverlay.id, ConstraintSet.BOTTOM, constraintLayout.id, ConstraintSet.BOTTOM, 0)
    constraintSet.connect(bOverlay.id, ConstraintSet.LEFT, constraintLayout.id, ConstraintSet.LEFT, 0)
    constraintSet.connect(bOverlay.id, ConstraintSet.RIGHT, constraintLayout.id, ConstraintSet.RIGHT, 0)
    constraintSet.applyTo(constraintLayout)
}

fun removeOverlay(activity: FragmentActivity) {
    val bOverlay: Button? = activity.findViewById(R.id.overlay_id)
    if (bOverlay != null) {
        val parent = bOverlay.parent as ViewGroup
        parent.removeView(bOverlay)
    }
}