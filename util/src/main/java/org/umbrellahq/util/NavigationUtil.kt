package org.umbrellahq.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log

/**
 * Created by mjanki on 2/18/18.
 */

const val TAG = "NavigationUtil"

object NavigationUtil {
    var mainResId: Int = -1

    fun setup(mainResId: Int) {
        this.mainResId = mainResId
    }
}

fun FragmentManager.pushFragment(fragment: Fragment) {
    if (NavigationUtil.mainResId == -1) {
        Log.e(TAG, "Setup Main Res Id before using pushFragment")
        return
    }

    beginTransaction().add(NavigationUtil.mainResId, fragment).commit()
}