package org.umbrellahq.baseapp.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.inflate


class SecondaryFragment : Fragment() {
    companion object {
        fun newInstance(): SecondaryFragment {
            return SecondaryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_secondary)
    }
}
