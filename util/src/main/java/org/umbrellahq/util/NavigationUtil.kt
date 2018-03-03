package org.umbrellahq.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import kotlin.reflect.KClass

/**
 * Created by mjanki on 2/18/18.
 */

object NavigationUtil {
    const val TAG = "NavigationUtil"

    var mainResId: Int = -1

    fun setup(mainResId: Int) {
        this.mainResId = mainResId
    }
}

fun AppCompatActivity.setupToolbar(toolbar: Toolbar, showUp: Boolean = true, title: String = "") {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
    this.title = title
}

/* Handle Push */
fun FragmentActivity.push(cls: KClass<*>, bundle: Bundle? = null, code: Int? = null, fragment: Fragment? = null) {
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

fun Fragment.push(cls: KClass<*>, bundle: Bundle? = null, code: Int? = null) {
    activity?.push(cls, bundle, code, this)
}

fun FragmentActivity.push(fragment: Fragment, addToBackStack: Boolean = true, fragmentTag: String? = null) {
    if (NavigationUtil.mainResId == -1) {
        Log.e(NavigationUtil.TAG, "Setup Main Res Id before using push")
        return
    }

    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(NavigationUtil.mainResId, fragment)

    if (addToBackStack) transaction.addToBackStack(fragmentTag)

    transaction.commit()
}

fun Fragment.push(fragment: Fragment, addToBackStack: Boolean = true, fragmentTag: String? = null) {
    activity?.push(fragment, addToBackStack, fragmentTag)
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