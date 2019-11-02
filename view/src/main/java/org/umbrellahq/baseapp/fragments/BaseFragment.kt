package org.umbrellahq.baseapp.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.umbrellahq.util.helper.removeOverlay

open class BaseFragment : Fragment() {
    override fun onResume() {
        super.onResume()

        if (activity != null) { removeOverlay(activity as FragmentActivity) }
    }
}