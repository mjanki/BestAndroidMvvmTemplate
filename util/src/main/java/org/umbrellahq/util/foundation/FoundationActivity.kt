package org.umbrellahq.util.foundation

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import org.umbrellahq.util.pop
import org.umbrellahq.util.removeOverlay

/**
 * Created by mjanki on 2/18/18.
 */
open class FoundationActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()

        removeOverlay()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> consume { pop() }
        else -> super.onOptionsItemSelected(item)
    }

    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }
}