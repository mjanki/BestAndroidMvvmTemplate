package org.umbrellahq.util

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

fun FragmentManager.pushFragment(fragment: Fragment) {
    if (NavigationUtil.mainResId == -1) {
        Log.e(NavigationUtil.TAG, "Setup Main Res Id before using pushFragment")
        return
    }

    beginTransaction().add(NavigationUtil.mainResId, fragment).commit()
}

fun FragmentActivity.pushActivity(cls: KClass<*>, bundle: Bundle? = null) {
    startActivity(Intent(this, cls.java).apply {
        if (bundle != null) putExtras(bundle)
    })
}

fun AppCompatActivity.setupToolbar(toolbar: Toolbar, showUp: Boolean = true, title: String = "") {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
    this.title = title
}

fun AppCompatActivity.popActivity() {
    try {
        NavUtils.navigateUpFromSameTask(this)
    } catch (e: IllegalArgumentException) {
        Log.e(NavigationUtil.TAG, "Setup android:parentActivityName in Activity's Manifest")
    }
}
