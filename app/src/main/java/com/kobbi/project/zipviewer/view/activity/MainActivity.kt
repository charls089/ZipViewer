package com.kobbi.project.zipviewer.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kobbi.project.zipviewer.R
import com.kobbi.project.zipviewer.databinding.ActivityMainBinding
import com.kobbi.project.zipviewer.viewmodel.DirViewModel

class MainActivity : BaseActivity() {

    private var mDirViewModel: DirViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).run {
            mDirViewModel =
                ViewModelProviders.of(this@MainActivity)[DirViewModel::class.java].apply {
                    showView.observe(this@MainActivity, Observer {
                        Intent(applicationContext, ViewPageActivity::class.java).run {
                            putExtra("path", it)
                            startActivity(this)
                            finish()
                        }
                    })
                    intent?.extras?.getString("path")?.let {
                        setItems(it)
                    }
                }
            dirVm = mDirViewModel
            lifecycleOwner = this@MainActivity
        }
    }

    override fun onBackPressed() {
        mDirViewModel?.run {
            if (isRootPath())
                super.onBackPressed()
            else
                goToPrevPath()
        } ?: super.onBackPressed()
    }
}
