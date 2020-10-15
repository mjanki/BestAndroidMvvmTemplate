package org.umbrellahq.util

import android.view.View

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) { View.VISIBLE } else { View.GONE }
}