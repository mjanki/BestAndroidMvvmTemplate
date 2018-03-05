package org.umbrellahq.util

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button

/**
 * Created by mjanki on 2/18/18.
 */
open class FoundationActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()

        val bOverlay: Button? = findViewById(R.id.overlay_id)
        if (bOverlay != null) {
            val parent = bOverlay.parent as ViewGroup
            parent.removeView(bOverlay)
        }
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