package org.umbrellahq.baseapp.activities

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.baseapp.models.ErrorNetworkViewEntity
import org.umbrellahq.util.setupToolbar

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar(toolbar, showUp = false, title = getString(R.string.app_name))
    }

    override fun handleErrorNetwork(errorNetworkViewEntity: ErrorNetworkViewEntity) {
        Snackbar.make(
                clContainer,
                "${errorNetworkViewEntity.action} failed with ${errorNetworkViewEntity.code}",
                Snackbar.LENGTH_LONG
        ).addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                resolveErrorNetwork()
            }
        }).show()
    }
}