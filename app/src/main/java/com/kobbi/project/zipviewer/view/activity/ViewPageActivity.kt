package com.kobbi.project.zipviewer.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kobbi.project.zipviewer.R
import com.kobbi.project.zipviewer.databinding.ActivityViewPageBinding
import com.kobbi.project.zipviewer.utils.Utils
import com.kobbi.project.zipviewer.viewmodel.FileViewModel

class ViewPageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fileViewModel =
            ViewModelProviders.of(this@ViewPageActivity)[FileViewModel::class.java].apply {
                applicationContext?.let { context ->
                    val savePath = Utils.getPath(context)
                    val savePage = Utils.getPage(context)
                    intent?.extras?.getString("path")?.let {
                        Utils.setPath(context, it)
                        this.setItems(it)
                    } ?: if (savePath.isNotEmpty())
                        this.setItems(savePath, savePage)
                    position.observe(this@ViewPageActivity, Observer {
                        Utils.setPage(context, it)
                    })
                }
            }
        DataBindingUtil.setContentView<ActivityViewPageBinding>(
            this, R.layout.activity_view_page
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
                        putExtra("path", Utils.getPath(applicationContext))
                    }
                }
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
