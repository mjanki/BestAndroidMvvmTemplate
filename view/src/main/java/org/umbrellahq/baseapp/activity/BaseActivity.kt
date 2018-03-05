package org.umbrellahq.baseapp.activity

import android.os.Bundle
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.foundation.FoundationActivity
import org.umbrellahq.util.NavigationUtil

open class BaseActivity : FoundationActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup main fragment id and main constraint layout id in NavigationUtil
        NavigationUtil.setup(R.id.flContainer, R.id.clContainer)
    }
}