package com.example.audioplayer.ui.base

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    fun showMessage(message: String) {
        Snackbar
            .make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
            .show()
    }
}