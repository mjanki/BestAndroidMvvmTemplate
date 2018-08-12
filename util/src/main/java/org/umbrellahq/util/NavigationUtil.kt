package org.umbrellahq.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import org.umbrellahq.util.helper.addOverlay
import org.umbrellahq.util.helper.createSceneTransitionAnimation

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

/* Handle Push Activity */
@SuppressLint("RestrictedApi")
inline fun <reified T: AppCompatActivity> FragmentActivity.push(
        bundle: Bundle? = null,
        code: Int? = null,
        fragment: Fragment? = null,
        blocking: Boolean = true,
        sharedView: View? = null,
        sharedViews: ArrayList<View>? = null) {

    // Add overlay if blocking
    if (blocking) addOverlay(this)

    // Create Intent, with extras if available
    val intent = Intent(this, T::class.java).apply {
        if (bundle != null) putExtras(bundle)
    }

    val optionsBundle: ActivityOptionsCompat? = createSceneTransitionAnimation(this, sharedView, sharedViews)

    // Handler().post will make sure the overlay is put instantly before animating
    Handler().post {
        if (code != null) {
            // Start activity for result either from Fragment or Activity
            if (fragment != null) startActivityFromFragment(fragment, intent, code, optionsBundle?.toBundle())
            else startActivityForResult(intent, code, optionsBundle?.toBundle())
        } else startActivity(intent, optionsBundle?.toBundle())
    }
}

inline fun <reified T: AppCompatActivity> Fragment.push(
        bundle: Bundle? = null,
        code: Int? = null,
        blocking: Boolean = true,
        sharedView: View? = null,
        sharedViews: ArrayList<View>? = null) {

    activity?.push<T>(bundle, code, this, blocking, sharedView, sharedViews)
}

/* Handle Push Fragment */
fun FragmentActivity.push(fragment: Fragment, isMainFragment: Boolean = false, fragmentTag: String? = null, blocking: Boolean = true) {
    // Add overlay if blocking
    if (blocking && !isMainFragment) addOverlay(this)

    if (NavigationUtil.fragmentLayoutResId == -1) {
        Log.e(NavigationUtil.TAG, "Setup Fragment Layout Res Id before using push for fragments")
        return
    }

    val transaction = supportFragmentManager.beginTransaction()
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
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
    if (intent != null) setResult(Activity.RESULT_OK, intent)
    if (supportFragmentManager.backStackEntryCount == 0) onBackPressed() else finish()

    //TODO: Research how to preserve animations on back without using onBackPressed() or finish()
    /*try {
        // Navigate up if no result is expected
        NavUtils.navigateUpFromSameTask(this)
    } catch (e: IllegalArgumentException) {
        Log.e(NavigationUtil.TAG, "Setup android:parentActivityName in Activity's Manifest")
    }*/
}