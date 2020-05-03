package com.kobbi.project.zipviewer.presenter.listener

import android.view.View


interface LongClickListener {
    fun onItemLongClick(position: Int, view: View)
}