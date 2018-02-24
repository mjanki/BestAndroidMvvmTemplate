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

fun Fragment.pushActivity(cls: KClass<*>, bundle: Bundle? = null, code: Int? = null) {
    val intent = Intent(activity, cls.java).apply {
        if (bundle != null) putExtras(bundle)
    }

    if (code == null) startActivity(intent) else activity?.startActivityFromFragment(this, intent, code)
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

fun FragmentManager.pushFragment(fragment: Fragment) {
    if (NavigationUtil.mainResId == -1) {
        Log.e(NavigationUtil.TAG, "Setup Main Res Id before using pushFragment")
        return
    }

    beginTransaction().add(NavigationUtil.mainResId, fragment).commit()
}