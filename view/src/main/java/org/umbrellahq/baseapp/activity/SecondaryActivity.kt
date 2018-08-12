package org.umbrellahq.baseapp.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.baseapp.fragment.SecondaryFragment
import org.umbrellahq.util.push
import org.umbrellahq.util.setupToolbar

class SecondaryActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar(toolbar, title = getString(R.string.fragment_name_secondary))

        if (savedInstanceState != null) { return }

        push(SecondaryFragment.newInstance(intent.extras), isMainFragment = true)
    }
}