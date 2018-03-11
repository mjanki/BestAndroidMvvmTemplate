package org.umbrellahq.util.foundation

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import org.umbrellahq.util.helper.removeOverlay
import org.umbrellahq.util.pop

open class FoundationActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()

        removeOverlay(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> consume { pop() }
        else -> super.onOptionsItemSelected(item)
    }

    private inline fun consume(block: () -> Unit): Boolean {
        block()
        return true
    }
}