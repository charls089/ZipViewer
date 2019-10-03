package com.kobbi.project.zipviewer.viewmodel

import android.app.Application
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kobbi.project.zipviewer.utils.SingleLiveEvent
import java.io.BufferedInputStream
import java.io.File
import java.io.FileFilter
import java.util.zip.ZipFile

class DirViewModel(application: Application) : AndroidViewModel(application) {
    val currentPath: LiveData<String> get() = _currentPath
    val currrentItems: LiveData<List<File>> get() = _currentItems
    val showView: SingleLiveEvent<String> get() = _showView
    private val _currentPath: MutableLiveData<String> = MutableLiveData()
    private val _currentItems: MutableLiveData<List<File>> = MutableLiveData()
    private val _showView: SingleLiveEvent<String> = SingleLiveEvent()

    private var rootPath = Environment.getExternalStorageDirectory().toString()

    private val mSelectedDir = mutableListOf<String>()
    private val mExternalPath = application.applicationContext.getExternalFilesDir(null)

    init {
        setItems(rootPath)
    }

    fun clickItem(position: Int) {
        Log.e("####", "clickItem() --> $position")
        _currentItems.value?.get(position)?.let {
            Log.e("####", "clickItem() --> item : $it")
            if (it.isDirectory) {
                mSelectedDir.add(it.name)
                setItems(getCurrentPath())
            } else if (it.extension.endsWith("zip")) {
                //unzip
                unzip(it, mExternalPath)
            } else if (it.extension.endsWith("png") ||
                it.extension.endsWith("jpg") ||
                it.extension.endsWith("gif")
            ) {
                _showView.call(it.parent)
            }
        }
    }

    fun goToPrevPath() {
        if (mSelectedDir.isNotEmpty())
            mSelectedDir.removeAt(mSelectedDir.size - 1)
        Log.e("####", "mSelectedDir : $mSelectedDir")
        setItems(getCurrentPath())
    }

    private fun setItems(path: String) {
        val filterList = File(path).listFiles(FileFilter {
            it.isDirectory ||
                    it.extension.endsWith("zip") ||
                    it.extension.endsWith("png") ||
                    it.extension.endsWith("jpg") ||
                    it.extension.endsWith("gif")
        })?.toList()
        Log.e("####", " filterList = $filterList")
        _currentItems.postValue(filterList)
        _currentPath.postValue(path)
    }

    private fun getCurrentPath(): String {
        var selectPath = ""
        mSelectedDir.forEach {
            selectPath += "/$it"
        }
        return rootPath + selectPath
    }

    private fun unzip(zipFile: File, targetPath: File?) {
        Log.e("####", "targetPath : $targetPath")
        targetPath?.let {
            val dir = File(targetPath, zipFile.nameWithoutExtension).apply {
                //                if (exists())
//                    return
                mkdirs()
            }
            val zip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                ZipFile(zipFile, charset("euc-kr"))
            else
                ZipFile(zipFile)
            val enumeration = zip.entries()
            while (enumeration.hasMoreElements()) {
                val entry = enumeration.nextElement()
                val destFilePath = File(dir, entry.name)
                Log.e("####", "entry.name : ${entry.name}")
                Log.e("####", "destFilePath : ${destFilePath.path}")
                destFilePath.parentFile.mkdirs()
                if (entry.isDirectory)
                    continue
                BufferedInputStream(zip.getInputStream(entry)).use { bis ->
                    destFilePath.outputStream().buffered(1024).use { bos ->
                        bis.copyTo(bos)
                    }
                }
            }
            mSelectedDir.clear()
            rootPath = dir.path
            setItems(dir.path)
        }
    }
}