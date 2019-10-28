package org.umbrellahq.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

object NavigationUtil {
    const val TAG = "NavigationUtil"

    var constraintLayoutResId: Int = -1

    fun setup(constraintLayoutResId: Int) {
        this.constraintLayoutResId = constraintLayoutResId
    }
}

fun AppCompatActivity.setupToolbar(toolbar: Toolbar, showUp: Boolean = true, title: String = "") {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
    this.title = title
}