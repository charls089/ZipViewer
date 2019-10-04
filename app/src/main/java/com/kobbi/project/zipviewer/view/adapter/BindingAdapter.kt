package com.kobbi.project.zipviewer.view.adapter

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.kobbi.project.zipviewer.R
import com.kobbi.project.zipviewer.utils.Utils
import com.kobbi.project.zipviewer.viewmodel.DirViewModel
import com.kobbi.weather.info.presenter.listener.ClickListener
import java.io.File
import java.lang.StringBuilder

class BindingAdapter private constructor() {
    companion object {

        @BindingAdapter("app:setViewPager", "app:setItems", "app:setListener")
        @JvmStatic
        fun setViewPager(
            view: ViewPager,
            fragmentManager: FragmentManager?,
            items: List<File>?,
            listener: ViewPager.OnPageChangeListener
        ) {
            fragmentManager?.let {
                items?.let {
                    view.adapter?.run {
                        if (this is ViewPagerAdapter) {
                            this.setItems(items)
                        }
                    } ?: kotlin.run {
                        view.apply {
                            adapter = ViewPagerAdapter(fragmentManager, items)
                            addOnPageChangeListener(listener)
                            view.context?.let { context ->
                                currentItem = Utils.getPage(context)
                            }
                        }
                    }
                }
            }
        }

        @BindingAdapter("app:setDir", "app:setVm")
        @JvmStatic
        fun setDirectory(
            recyclerView: RecyclerView,
            items: List<File>?,
            dirVm: DirViewModel?
        ) {
            if (items != null && dirVm != null) {
                recyclerView.adapter?.run {
                    if (this is DirAdapter)
                        setItems(items)
                } ?: kotlin.run {
                    val adapter = DirAdapter(items).apply {
                        setOnClickListener(object : ClickListener {
                            override fun onItemClick(position: Int, view: View) {
                                view.context.applicationContext?.let {
                                    dirVm.clickItem(position)
                                }
                            }
                        })
                    }
                    recyclerView.adapter = adapter
                }
                recyclerView.context?.let { context ->
                    val position = Utils.getPage(context)
                    recyclerView.scrollToPosition(position)
                }
            }
        }

        @BindingAdapter("app:setImg", "app:getFile")
        @JvmStatic
        fun setImg(view: ImageView, img: Bitmap?, file: File) {
            with(view) {
                if (file.isDirectory)
                    setImageResource(R.drawable.baseline_folder_white_48)
                else {
                    file.run {
                        if (Utils.isPictureFile(this)) {
                            setImageBitmap(img)
                        } else {
                            setImageResource(R.drawable.baseline_inbox_24)
                        }
                    }
                }
            }
        }

        @BindingAdapter("app:setTotal", "app:setPosition")
        @JvmStatic
        fun setTotal(view: TextView, items: List<File>?, position: Int) {
            items?.let {
                view.text = "${position + 1} / ${it.size}"
            }
        }

        @BindingAdapter("app:setViewImg")
        @JvmStatic
        fun setViewImg(view: ImageView, path: String?) {
            path?.let {
                val file = File(path)
                if (file.exists()) {
                    val bitmap = Utils.getBitmap(file, 1440, 1080)
                    bitmap?.run {
                        view.setImageBitmap(this)
                    }
                }
            }
        }

        @BindingAdapter("app:setCurrentPath")
        @JvmStatic
        fun setCurrentPath(view: TextView, path: String?) {
            Log.e("####", "path : $path")
            path?.let {
                val rootPath = Environment.getExternalStorageDirectory().path
                val isStart = it.startsWith(rootPath)
                val f = File(path)
                val nPath = if (!f.isDirectory) f.parent else f.path
                Log.e("####", "nPath : $nPath")

                val sb = StringBuilder()
                val removeRootPath = if (isStart) {
                    sb.append("내장 메모리")
                    nPath.drop(rootPath.length + 1)
                } else {
                    nPath
                }
                Log.e("####", "isStart : $isStart / removeRootPath : $removeRootPath")
                val list = removeRootPath.split('/')
                list.forEach { folderName ->
                    sb.append(" >> ")
                    sb.append(folderName)
                }
                view.text = sb.toString()
            }
        }

        @BindingAdapter("app:getFileSize")
        @JvmStatic
        fun getFileSize(view: TextView, length: Long?) {
            length?.let {
                var devide = 1024
                var count = 0
                if (length >= devide) {
                    devide *= 1024
                    count++
                    if (length >= devide) {
                        devide *= 1024
                        count++
                        if (length >= 1024) {
                            devide *= 1024
                            count++
                        }
                    }
                }
                val b = length / devide
                val suffix = when(count) {
                    0 -> "byte"
                    1 -> "Kb"
                    2 -> "Mb"
                    3 -> "Gb"
                    else -> "byte"
                }
            }
        }
    }
}