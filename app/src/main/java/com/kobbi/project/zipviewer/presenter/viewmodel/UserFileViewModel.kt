package com.kobbi.project.zipviewer.presenter.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kobbi.project.zipviewer.data.database.entity.FileProperties
import com.kobbi.project.zipviewer.data.database.entity.UserFile
import com.kobbi.project.zipviewer.presenter.file.FileManager
import com.kobbi.project.zipviewer.presenter.listener.ClickListener
import java.io.File

class UserFileViewModel(application: Application) : AndroidViewModel(application) {
    val useFile: LiveData<UserFile> get() = _useFile
    val fileProperties: LiveData<FileProperties> get() = _fileProperties
    val currentPathList: LiveData<List<String>> get() = _currentPathList
    val clickListener = object : ClickListener {
        override fun onItemClick(position: Int, view: View) {
            selectItem(position)
        }
    }

    private val _currentFiles: MutableLiveData<List<File>> = MutableLiveData()
    private val _currentPathList: MutableLiveData<List<String>> = MutableLiveData()

    private val mFileManager by lazy { FileManager(application) }
    private var _useFile: LiveData<UserFile> = mFileManager.getUseFile("")
    private var _fileProperties: LiveData<FileProperties> = mFileManager.getFileProperties("")


    /**
     * 탐색기에서 파일 선택 시 호출됨.
     * @param position 선택한 파일의 위치
     */
    fun selectItem(position: Int) {
        _currentFiles.value?.get(position)?.let { file ->
            with(mFileManager) {
                getFileInfo(file).let { fileInfo ->
                    if (fileInfo.fileType.extensions.isEmpty()) {
                        _currentFiles.postValue(getChildFiles(fileInfo.file))
                    } else {
                        fileInfo.file?.nameWithoutExtension?.let { fileName ->
                            _useFile = getUseFile(fileName)
                            _fileProperties = getFileProperties(fileName)
                        }
                    }
                }
            }
        }
    }

    /**
     * 파일 경로 갱신.
     * @param file 현재 파일
     */
    fun setItemPathList(file: File) {
        _currentPathList.postValue(
            mFileManager.getItemPathList(file)
        )
    }

    /**
     * 선택한 파일 보여주기.
     * @param file 선택한 파일
     * @param position 선택한 파일 위치
     */
    fun showView(file: File, position: Int) {
        mFileManager.updateLastViewPage(file, position)
    }
}