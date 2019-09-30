package com.kobbi.project.zipviewer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import java.io.File

class FileViewModel(application: Application) : AndroidViewModel(application) {
    val fileList: LiveData<List<File>> get() = _fileList
    val position: LiveData<Int> get() = _position
    val state: LiveData<Int> get() = _state

    private val _fileList: MutableLiveData<List<File>> = MutableLiveData()
    private val _position: MutableLiveData<Int> = MutableLiveData()
    private val _state: MutableLiveData<Int> = MutableLiveData()

    val listener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            _state.postValue(state)
        }

        override fun onPageScrolled(
            position: Int, positionOffset: Float, positionOffsetPixels: Int
        ) {
            //Nothing
        }

        override fun onPageSelected(position: Int) {
            _position.postValue(position)
        }
    }
}