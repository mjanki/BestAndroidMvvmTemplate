package org.umbrellahq.util.foundation

import androidx.appcompat.app.AppCompatActivity
import org.umbrellahq.util.helper.removeOverlay

open class FoundationActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()

        removeOverlay(this)
    }
}