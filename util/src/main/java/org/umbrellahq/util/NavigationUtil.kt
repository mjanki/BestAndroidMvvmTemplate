package org.umbrellahq.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
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

// Go to new activity
fun FragmentActivity.pushActivity(cls: KClass<*>, bundle: Bundle? = null, code: Int? = null, fragment: Fragment? = null) {
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

// Add ability to call pushActivity from Fragment
fun Fragment.pushActivity(cls: KClass<*>, bundle: Bundle? = null, code: Int? = null) {
    activity?.pushActivity(cls, bundle, code, this)
}

// Navigate up
fun FragmentActivity.popActivity(intent: Intent? = null) {
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

// Add ability to call popActivity from Fragment
fun Fragment.popActivity(intent: Intent? = null) {
    activity?.popActivity(intent)
}

fun FragmentActivity.pushFragment(fragment: Fragment, addToBackStack: Boolean = true, fragmentTag: String? = null) {
    if (NavigationUtil.mainResId == -1) {
        Log.e(NavigationUtil.TAG, "Setup Main Res Id before using pushFragment")
        return
    }

    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(NavigationUtil.mainResId, fragment)

    if (addToBackStack) transaction.addToBackStack(fragmentTag)

    transaction.commit()
}

fun Fragment.pushFragment(fragment: Fragment, addToBackStack: Boolean = true, fragmentTag: String? = null) {
    activity?.pushFragment(fragment, addToBackStack, fragmentTag)
}