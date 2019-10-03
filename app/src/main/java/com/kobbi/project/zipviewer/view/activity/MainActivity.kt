package com.kobbi.project.zipviewer.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kobbi.project.zipviewer.R
import com.kobbi.project.zipviewer.databinding.ActivityMainBinding
import com.kobbi.project.zipviewer.viewmodel.DirViewModel

class MainActivity : AppCompatActivity() {

    private var mDirViewModel: DirViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).run {
            mDirViewModel = ViewModelProviders.of(this@MainActivity)[DirViewModel::class.java].apply {
                showView.observe(this@MainActivity, Observer {
                    startActivity(Intent(applicationContext, FullscreenActivity::class.java))
                })
            }
            dirVm = mDirViewModel
            lifecycleOwner = this@MainActivity
        }
    }

    override fun onBackPressed() {
        if (mDirViewModel?.currentPath?.value == Environment.getExternalStorageDirectory().toString())
            finish()
        else
            mDirViewModel?.goToPrevPath()
    }
}
