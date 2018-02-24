package org.umbrellahq.baseapp.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_secondary.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.inflate


class SecondaryFragment : Fragment() {
    companion object {
        const val EXTRA_NAME = "name"

        fun newInstance(extras: Bundle?): SecondaryFragment {
            val secondaryFragment = SecondaryFragment()

            if (extras != null) {
                secondaryFragment.text = extras.getString(EXTRA_NAME)
            }

            return secondaryFragment
        }
    }

    var text: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_secondary)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMain.text = text
    }
}
