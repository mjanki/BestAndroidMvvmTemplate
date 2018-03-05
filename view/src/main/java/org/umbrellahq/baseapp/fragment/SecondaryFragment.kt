package org.umbrellahq.baseapp.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_secondary.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.foundation.FoundationFragment
import org.umbrellahq.util.inflate
import org.umbrellahq.util.pop
import org.umbrellahq.util.push

class SecondaryFragment : FoundationFragment() {
    companion object {
        const val EXTRA_NAME = "name"

        fun newInstance(extras: Bundle?): SecondaryFragment {
            val secondaryFragment = SecondaryFragment()

            if (extras != null) {
                secondaryFragment.text = extras.getString(EXTRA_NAME, secondaryFragment.text)
            }

            return secondaryFragment
        }
    }

    var text: String = "No Value"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_secondary)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvMain.text = text

        bMain.setOnClickListener {
            pop(Intent().putExtra(MainFragment.EXTRA_NAME, "This is extra returned!"))
        }

        bFragment.setOnClickListener {
            push(ThirdFragment.newInstance())
        }
    }
}
