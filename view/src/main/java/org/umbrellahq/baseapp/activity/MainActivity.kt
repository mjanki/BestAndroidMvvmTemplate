package org.umbrellahq.baseapp.activity

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.baseapp.fragment.MainFragment
import org.umbrellahq.util.push
import org.umbrellahq.util.setupToolbar
import org.umbrellahq.viewmodel.model.ErrorNetworkViewModelEntity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar(toolbar, showUp = false, title = getString(R.string.app_name))

        if (savedInstanceState != null) { return }

        push(MainFragment.newInstance(), isMainFragment = true)
    }

    override fun handleErrorNetwork(errorNetworkViewModelEntity: ErrorNetworkViewModelEntity) {
        Snackbar.make(
                clContainer,
                "${errorNetworkViewModelEntity.action} failed with ${errorNetworkViewModelEntity.code}",
                Snackbar.LENGTH_LONG
        ).addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                resolveErrorNetwork()
            }
        }).show()
    }
}