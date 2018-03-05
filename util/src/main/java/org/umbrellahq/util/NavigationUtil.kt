package org.umbrellahq.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import kotlin.reflect.KClass

object NavigationUtil {
    const val TAG = "NavigationUtil"

    var fragmentLayoutResId: Int = -1
    var constraintLayoutResId: Int = -1

    fun setup(fragmentLayoutResId: Int, constraintLayoutResId: Int) {
        this.fragmentLayoutResId = fragmentLayoutResId
        this.constraintLayoutResId = constraintLayoutResId
    }
}

fun AppCompatActivity.setupToolbar(toolbar: Toolbar, showUp: Boolean = true, title: String = "") {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
    this.title = title
}

/* Handle Push */
fun FragmentActivity.push(cls: KClass<*>, bundle: Bundle? = null, code: Int? = null, fragment: Fragment? = null, blocking: Boolean = true) {
    // Add overlay if blocking
    if (blocking) addOverlay()

    // Create Intent, with extras if available
    val intent = Intent(this, cls.java).apply {
        if (bundle != null) putExtras(bundle)
    }

    if (code != null) {
        // Start activity for result either from Fragment or Activity
        if (fragment != null) startActivityFromFragment(fragment, intent, code)
        else startActivityForResult(intent, code)
    } else startActivity(intent)
}

fun Fragment.push(cls: KClass<*>, bundle: Bundle? = null, code: Int? = null, blocking: Boolean = true) {
    activity?.push(cls, bundle, code, this, blocking)
}

fun FragmentActivity.push(fragment: Fragment, isMainFragment: Boolean = false, fragmentTag: String? = null, blocking: Boolean = true) {
    // Add overlay if blocking
    if (blocking && !isMainFragment) addOverlay()

    if (NavigationUtil.fragmentLayoutResId == -1) {
        Log.e(NavigationUtil.TAG, "Setup Fragment Layout Res Id before using push for fragments")
        return
    }

    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(NavigationUtil.fragmentLayoutResId, fragment)

    if (!isMainFragment) transaction.addToBackStack(fragmentTag)

    transaction.commit()
}

fun Fragment.push(fragment: Fragment, isMainFragment: Boolean = false, fragmentTag: String? = null, blocking: Boolean = true) {
    activity?.push(fragment, isMainFragment, fragmentTag, blocking)
}

/* Handle Pop */
fun FragmentActivity.pop(intent: Intent? = null, forcePopActivity: Boolean = false, fragmentTag: String? = null, popInclusive: Boolean = false) {
    if (intent != null || supportFragmentManager.backStackEntryCount == 0 || forcePopActivity) {
        popActivity(intent)
    } else if (fragmentTag != null && supportFragmentManager.findFragmentByTag(fragmentTag) != null) {
        supportFragmentManager.popBackStack(fragmentTag, if (popInclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0)
    } else {
        supportFragmentManager.popBackStack()
    }
}

fun Fragment.pop(intent: Intent? = null, forcePopActivity: Boolean = false, fragmentTag: String? = null, popInclusive: Boolean = false) {
    activity?.pop(intent, forcePopActivity, fragmentTag, popInclusive)
}

private fun FragmentActivity.popActivity(intent: Intent? = null) {
    if (intent != null) {
        // Send results back to prior activity
        setResult(Activity.RESULT_OK, intent)
        finish()
    } else {
        try {
            // Navigate up if no result is expected
            NavUtils.navigateUpFromSameTask(this)
        } catch (e: IllegalArgumentException) {
            Log.e(NavigationUtil.TAG, "Setup android:parentActivityName in Activity's Manifest")
        }
    }
}

fun FragmentActivity.removeOverlay() {
    val bOverlay: Button? = findViewById(R.id.overlay_id)
    if (bOverlay != null) {
        val parent = bOverlay.parent as ViewGroup
        parent.removeView(bOverlay)
    }
}

private fun FragmentActivity.addOverlay() {
    if (NavigationUtil.constraintLayoutResId == -1) {
        Log.e(NavigationUtil.TAG, "Setup Constraint Layout Res Id for blocking effect")
        return
    }

    // Remove overlay if exists before adding again
    removeOverlay()

    // Add Overlay
    val constraintLayout = findViewById<ConstraintLayout>(NavigationUtil.constraintLayoutResId)

    val bOverlay = Button(this)
    bOverlay.id = R.id.overlay_id
    bOverlay.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
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