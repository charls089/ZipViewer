package com.kobbi.project.zipviewer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.ViewPager
import com.kobbi.project.zipviewer.utils.Utils
import java.io.File

class FileViewModel : ViewModel() {
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

    fun setItems(path: String, page: Int = -1) {
        val f = File(path)
        val file = if (f.isDirectory) f else f.parentFile
        val fileList = file.listFiles().filter {
            Utils.isPictureFile(it)
        }
        _fileList.postValue(fileList)
        if (page == -1) {
            val lastPosition = fileList.indexOf(f)
            if (lastPosition != -1)
                _position.postValue(lastPosition)
        } else {
            _position.postValue(page)
        }
    }
}