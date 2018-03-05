package org.umbrellahq.util.foundation


import android.support.v4.app.Fragment
import org.umbrellahq.util.removeOverlay


open class FoundationFragment : Fragment() {
    override fun onResume() {
        super.onResume()

        activity?.removeOverlay()
    }
}
