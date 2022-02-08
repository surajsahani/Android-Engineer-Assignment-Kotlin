package com.example.audioplayer.util

import android.view.View

/**
 * Sets the visibility of view to [View.VISIBLE]
 */
fun View.show() {
    this.visibility = View.VISIBLE
}

/**
 * Sets the visibility of view to [View.INVISIBLE]
 */
fun View.hide() {
    this.visibility = View.INVISIBLE
}

/**
 * Sets the visibility of view to [View.GONE]
 */
fun View.gone() {
    this.visibility = View.INVISIBLE
}