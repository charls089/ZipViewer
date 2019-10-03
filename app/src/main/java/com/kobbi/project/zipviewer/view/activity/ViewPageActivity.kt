package com.kobbi.project.zipviewer.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kobbi.project.zipviewer.R
import com.kobbi.project.zipviewer.databinding.ActivityViewPageBinding
import com.kobbi.project.zipviewer.utils.SharedPrefHelper
import com.kobbi.project.zipviewer.viewmodel.FileViewModel

class ViewPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fileViewModel =
            ViewModelProviders.of(this@ViewPageActivity)[FileViewModel::class.java].apply {
                val savePath = SharedPrefHelper.getString(
                    applicationContext,
                    SharedPrefHelper.KEY_LAST_OPEN_FILE_PATH
                )
                intent?.extras?.getString("path")?.let {
                    SharedPrefHelper.setString(
                        applicationContext,
                        SharedPrefHelper.KEY_LAST_OPEN_FILE_PATH,
                        it
                    )
                    this.setItems(it)
                } ?: if (savePath.isNotEmpty())
                    this.setItems(savePath)
                position.observe(this@ViewPageActivity, Observer {
                    SharedPrefHelper.setInt(
                        applicationContext,
                        SharedPrefHelper.KEY_LAST_VIEW_FILE_PAGE,
                        it
                    )
                })
            }
        DataBindingUtil.setContentView<ActivityViewPageBinding>(
            this,
            R.layout.activity_view_page
        ).run {
            fileVm = fileViewModel
            fragmentManager = supportFragmentManager
            lifecycleOwner = this@ViewPageActivity
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_view_page, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_list, R.id.action_home -> {
                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    if (item.itemId == R.id.action_list) {
                        val path = SharedPrefHelper.getString(
                            applicationContext,
                            SharedPrefHelper.KEY_LAST_OPEN_FILE_PATH
                        )
                        putExtra("path", path)
                    }
                }
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
