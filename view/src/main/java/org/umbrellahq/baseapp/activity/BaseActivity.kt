package org.umbrellahq.baseapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.NavigationUtil
import org.umbrellahq.util.popActivity

/**
 * Created by mjanki on 2/18/18.
 */
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup main fragment id in NavigationUtil
        NavigationUtil.setup(R.id.flContainer)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> consume { popActivity() }
        else -> super.onOptionsItemSelected(item)
    }

    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }
}