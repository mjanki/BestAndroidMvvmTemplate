package org.umbrellahq.util.foundation

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.umbrellahq.util.helper.removeOverlay

open class FoundationFragment : Fragment() {
    override fun onResume() {
        super.onResume()

        if (activity != null) { removeOverlay(activity as FragmentActivity) }
    }
}