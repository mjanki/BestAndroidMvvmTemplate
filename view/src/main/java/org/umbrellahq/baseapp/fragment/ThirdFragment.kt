package org.umbrellahq.baseapp.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_third.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.foundation.FoundationFragment
import org.umbrellahq.util.inflate
import org.umbrellahq.util.pop


class ThirdFragment : FoundationFragment() {
    companion object {
        fun newInstance(): ThirdFragment {
            return ThirdFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_third)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMain.text = "This is the third Fragment!"

        bBack.setOnClickListener {
            pop(Intent().putExtra(MainFragment.EXTRA_NAME, "Forced Pop Activity"), forcePopActivity = true)
        }
    }
}
