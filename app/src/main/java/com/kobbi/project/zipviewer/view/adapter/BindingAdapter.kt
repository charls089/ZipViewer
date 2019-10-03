package com.kobbi.project.zipviewer.view.adapter

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.kobbi.project.zipviewer.DirAdapter
import com.kobbi.project.zipviewer.R
import com.kobbi.project.zipviewer.viewmodel.DirViewModel
import com.kobbi.weather.info.presenter.listener.ClickListener
import java.io.File

class BindingAdapter private constructor() {
    companion object {

        @BindingAdapter("app:setViewPager", "app:setItems")
        @JvmStatic
        fun setViewPager(view: ViewPager, fragmentManager: FragmentManager?, items: List<File>?) {
            fragmentManager?.let {
                items?.let {
                    view.adapter?.run {
                        if(this is ViewPagerAdapter) {
                            this.setItems(items)
                        }
                    }?:kotlin.run {
                        view.adapter = ViewPagerAdapter(fragmentManager,items)
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
            val resId =
                if (file.isDirectory) R.drawable.baseline_folder_white_48 else R.drawable.baseline_inbox_white_48
            view.setImageResource(resId)
        }
    }
}