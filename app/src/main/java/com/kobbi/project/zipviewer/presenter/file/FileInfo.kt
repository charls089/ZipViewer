package com.kobbi.project.zipviewer.presenter.file

import java.io.File

data class FileInfo(
    val file: File?,
    val fileType: FileManager.FileType
)