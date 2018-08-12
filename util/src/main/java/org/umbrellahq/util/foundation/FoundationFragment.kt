package org.umbrellahq.util.foundation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.umbrellahq.util.helper.removeOverlay

open class FoundationFragment : Fragment() {
    override fun onResume() {
        super.onResume()

        if (activity != null) { removeOverlay(activity as FragmentActivity) }
    }
}