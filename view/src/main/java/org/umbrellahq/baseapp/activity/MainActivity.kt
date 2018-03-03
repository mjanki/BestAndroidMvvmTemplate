package org.umbrellahq.baseapp.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.baseapp.fragment.MainFragment
import org.umbrellahq.util.pushFragment
import org.umbrellahq.util.setupToolbar

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar(toolbar, showUp = false, title = getString(R.string.app_name))

        if (savedInstanceState != null) { return }

        pushFragment(MainFragment.newInstance())
    }
}
