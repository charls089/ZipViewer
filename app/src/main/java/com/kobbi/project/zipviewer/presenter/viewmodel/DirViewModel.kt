package com.kobbi.project.zipviewer.presenter.viewmodel

import android.app.Application
import android.os.Build
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kobbi.project.zipviewer.presenter.file.FileManager
import com.kobbi.project.zipviewer.utils.SingleLiveEvent
import com.kobbi.project.zipviewer.utils.Utils
import java.io.BufferedInputStream
import java.io.File
import java.io.FileFilter
import java.util.zip.ZipFile

class DirViewModel(application: Application) : AndroidViewModel(application) {
    val currentPath: LiveData<String> get() = _currentPath
    val currentItems: LiveData<List<File>> get() = _currentItems
    val showView: SingleLiveEvent<String> get() = _showView
    private val _currentPath: MutableLiveData<String> = MutableLiveData()
    private val _currentItems: MutableLiveData<List<File>> = MutableLiveData()
    private val _showView: SingleLiveEvent<String> = SingleLiveEvent()

    val rootPath = Environment.getExternalStorageDirectory().toString()
    private val cachePath = application.applicationContext.cacheDir

    init {
        setItems(rootPath)
    }

    fun clickItem(position: Int) {
        _currentItems.value?.get(position)?.let {
            when {
                Utils.isDirectory(it) -> {
                    setItems(it.path)
                }
                Utils.isZipFile(it) -> unzip(it, cachePath)
                Utils.isPictureFile(it) -> _showView.call(it.path)
            }
        }
    }

    fun isRootPath(): Boolean {
        val value = currentPath.value
        return value == rootPath || value == cachePath.toString()
    }

    fun goToPrevPath() {
        val currentPath = _currentPath.value
        currentPath?.let {
            val prevPath = currentPath.substringBeforeLast('/')
            setItems(prevPath)
        }
    }

    fun setItems(path: String) {
        val f = File(path)
        val file = if (f.isDirectory) f else f.parentFile
        val filterList = file.listFiles(FileFilter {
            Utils.isDirectory(it) || Utils.isZipFile(it) || Utils.isPictureFile(it)
        })?.toList()?.sorted()
        _currentItems.postValue(filterList)
        _currentPath.postValue(file.path)
    }

    fun setItemPath(path: String): List<String> {
        return listOf()
    }

    private fun unzip(zipFile: File, targetPath: File?) {
        targetPath?.let {
            val dir = File(targetPath, zipFile.nameWithoutExtension).apply {
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
                destFilePath.parentFile.mkdirs()
                if (entry.isDirectory)
                    continue
                BufferedInputStream(zip.getInputStream(entry)).use { bis ->
                    destFilePath.outputStream().buffered(1024).use { bos ->
                        bis.copyTo(bos)
                    }
                }
            }
            _showView.call(dir.path)

        }
    }

    private fun extensionFilter(file: File): List<File> {
        val results = mutableSetOf<File>()
        file.listFiles().forEach {
            if (it.isDirectory)
                results.addAll(extensionFilter(it))
            else
                if (Utils.isZipFile(it) || Utils.isPictureFile(it)) {
                    results.add(it.parentFile)

                }
        }
        return results.toList()
    }
}