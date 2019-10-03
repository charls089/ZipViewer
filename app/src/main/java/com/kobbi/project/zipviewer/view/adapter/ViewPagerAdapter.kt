package com.kobbi.project.zipviewer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import java.io.File

class ViewPagerAdapter(fragmentManager: FragmentManager, items:List<File>) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mItems = mutableListOf<File>()

    init {
        mItems.addAll(items)

    }

    fun setItems(items: List<File>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return ViewFragment.newInstance(position)
    }

    override fun getItemPosition(any: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int = mItems.size
}