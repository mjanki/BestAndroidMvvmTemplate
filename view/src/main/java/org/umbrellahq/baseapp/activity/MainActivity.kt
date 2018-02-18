package org.umbrellahq.baseapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.umbrellahq.baseapp.R
import org.umbrellahq.baseapp.fragment.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        title = getString(R.string.app_name)

        if (savedInstanceState != null) { return }

        supportFragmentManager
                .beginTransaction()
                .add(R.id.flContainer, MainFragment.newInstance())
                .commit()
    }
}
