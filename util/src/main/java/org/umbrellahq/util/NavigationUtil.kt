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

fun FragmentActivity.pushActivity(cls: KClass<*>, bundle: Bundle? = null, code: Int? = null, fragment: Fragment? = null) {
    val intent = Intent(this, cls.java).apply {
        if (bundle != null) putExtras(bundle)
    }

    if (code != null) {
        if (fragment != null) startActivityFromFragment(fragment, intent, code)
        else startActivityForResult(intent, code)
    } else startActivity(intent)
}

fun Fragment.pushActivity(cls: KClass<*>, bundle: Bundle? = null, code: Int? = null) {
    activity?.pushActivity(cls, bundle, code, this)
}

fun FragmentActivity.popActivity(intent: Intent? = null) {
    if (intent != null) {
        setResult(Activity.RESULT_OK, intent)
        finish()
    } else {
        try {
            NavUtils.navigateUpFromSameTask(this)
        } catch (e: IllegalArgumentException) {
            Log.e(NavigationUtil.TAG, "Setup android:parentActivityName in Activity's Manifest")
        }
    }
}

fun Fragment.popActivity(intent: Intent? = null) {
    activity?.popActivity(intent)
}

fun FragmentManager.pushFragment(fragment: Fragment) {
    if (NavigationUtil.mainResId == -1) {
        Log.e(NavigationUtil.TAG, "Setup Main Res Id before using pushFragment")
        return
    }

    beginTransaction().add(NavigationUtil.mainResId, fragment).commit()
}