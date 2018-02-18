package org.umbrellahq.baseapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.umbrellahq.baseapp.R
import org.umbrellahq.util.NavigationUtil

/**
 * Created by mjanki on 2/18/18.
 */
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup main fragment id in NavigationUtil
        NavigationUtil.setup(R.id.flContainer)
    }
}