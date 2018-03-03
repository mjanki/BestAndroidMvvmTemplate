package org.umbrellahq.baseapp.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_secondary.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.inflate


class ThirdFragment : Fragment() {
    companion object {
        fun newInstance(extras: Bundle?): ThirdFragment {
            return ThirdFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_third)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMain.text = "This is the third Fragment!"
    }
}
