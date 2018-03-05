package org.umbrellahq.baseapp.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.baseapp.activity.SecondaryActivity
import org.umbrellahq.util.foundation.FoundationFragment
import org.umbrellahq.util.inflate
import org.umbrellahq.util.push

class MainFragment : FoundationFragment() {
    companion object {
        const val REQUEST_CODE_1 = 1
        const val EXTRA_NAME = "extra1"

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_main)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bSecondary.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(SecondaryFragment.EXTRA_NAME, "This is the new text!")
            push(SecondaryActivity::class, bundle, REQUEST_CODE_1)
        }
    }

    override fun onResume() {
        super.onResume()

        bSecondary.isEnabled = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_1 && resultCode == Activity.RESULT_OK) {
            val string = data?.getStringExtra(EXTRA_NAME)
            if (string != null) tvMain.text = string
        }
    }
}
