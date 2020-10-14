package org.umbrellahq.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

fun AppCompatActivity.setupToolbar(toolbar: Toolbar, showUp: Boolean = true, title: String = "") {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
    this.title = title
}