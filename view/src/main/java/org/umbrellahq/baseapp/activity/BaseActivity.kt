package org.umbrellahq.baseapp.activity

import android.os.Bundle
import android.transition.Fade
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.NavigationUtil
import org.umbrellahq.util.foundation.FoundationActivity

open class BaseActivity : FoundationActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup main fragment id and main constraint layout id in NavigationUtil
        NavigationUtil.setup(R.id.flContainer, R.id.clContainer)

        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)

        window.enterTransition = fade
        window.exitTransition = fade
    }
}