package com.kobbi.project.zipviewer.view.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.kobbi.project.zipviewer.utils.BackPressedCloser

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    private val mBackPressedCloser by lazy { BackPressedCloser(this) }

    override fun onBackPressed() {
        mBackPressedCloser.onBackPressed()
    }
}