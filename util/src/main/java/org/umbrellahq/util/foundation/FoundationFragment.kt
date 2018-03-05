package org.umbrellahq.util.foundation


import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.widget.Button
import org.umbrellahq.util.R


open class FoundationFragment : Fragment() {
    override fun onResume() {
        super.onResume()

        val bOverlay: Button? = activity?.findViewById(R.id.overlay_id)
        if (bOverlay != null) {
            val parent = bOverlay.parent as ViewGroup
            parent.removeView(bOverlay)
        }
    }
}
