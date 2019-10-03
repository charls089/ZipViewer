package com.kobbi.project.zipviewer.view.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.kobbi.project.zipviewer.R
import com.kobbi.project.zipviewer.utils.SharedPrefHelper
import com.kobbi.project.zipviewer.viewmodel.DirViewModel
import com.kobbi.weather.info.presenter.listener.ClickListener
import java.io.File

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
                            currentItem = SharedPrefHelper.getInt(
                                view.context,
                                SharedPrefHelper.KEY_LAST_VIEW_FILE_PAGE
                            )
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
            if (items != null && dirVm != null)
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
        }

        @BindingAdapter("app:setImg")
        @JvmStatic
        fun setImg(view: ImageView, file: File) {
            with(view) {
                if (file.isDirectory)
                    setImageResource(R.drawable.baseline_folder_white_48)
                else {
                    file.run {
                        if (extension.endsWith("jpg") ||
                            extension.endsWith("png") ||
                            extension.endsWith("gif")
                        ) {
                            val bitmap =
                                BitmapFactory.decodeFile(this.path, BitmapFactory.Options().apply {
                                    this.inSampleSize = 16
                                })
                            setImageBitmap(bitmap)
//                            setImageBitmap(Bitmap.createScaledBitmap(bitmap, 48, 48, false))
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
                    BitmapFactory.Options().inSampleSize
                    BitmapFactory.decodeFile(file.path).run {
                        view.setImageBitmap(
                            Bitmap.createScaledBitmap(
                                this,
                                this.width,
                                this.height,
                                false
                            )
                        )
                    }
                }
            }
        }
    }
}