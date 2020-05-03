package com.kobbi.project.zipviewer.presenter.file

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import com.kobbi.project.zipviewer.data.database.UserFileDatabase
import com.kobbi.project.zipviewer.data.database.entity.FileProperties
import com.kobbi.project.zipviewer.data.database.entity.UserFile
import java.io.BufferedInputStream
import java.io.File
import java.io.FileFilter
import java.util.zip.ZipFile

class FileManager(context: Context) {

    private val rootPath = context.applicationContext.getExternalFilesDir(null).toString()
    private val cacheDir = context.applicationContext.cacheDir.toString()
    private val userFileDB: UserFileDatabase by lazy {
        UserFileDatabase.run {
            setListener(object : UserFileDatabase.Listener {
                override fun onCreate() {
                    Log.i("###_FileManager", "DB was created.")
                }

                override fun onOpen() {
                    Log.i("###_FileManager", "DB was opened.")
                }
            })
            getDatabase(context)
        }
    }

    fun getFileInfo(file: File): FileInfo {
        val fileType = FileType.getType(file)
        val resultFile = when (FileType.getType(file)) {
            FileType.ZIP -> {
                unzip(file)
            }
            FileType.TXT -> {
                separateTextFile(file)
            }
            FileType.DIR -> {
                file
            }
            FileType.IMG -> {
                file.parentFile
            }
            else -> {
                null
            }
        }
        insertUserFile(resultFile, fileType)
        return FileInfo(resultFile, fileType)
    }

    fun getChildFiles(file: File?): List<File> {
        return file?.listFiles(FileFilter {
            FileType.getType(file) != FileType.UNKNOWN
        })?.toList()?.sorted() ?: emptyList()
    }

    fun insertUserFile(file: File?, type: FileType) {
        if (type.isSave && file?.exists() == true)
            userFileDB.userFileDao().insert(
                UserFile(file.nameWithoutExtension, file.absolutePath, type.name)
            )
    }

    fun updateLastViewPage(file: File, page: Int) {
        userFileDB.filePropertiesDao().let { dao ->
            if (dao.loadFromName(file.nameWithoutExtension) == null) {
                val totalPages = file.listFiles()?.size ?: 0
                dao.insert(
                    FileProperties(
                        file.nameWithoutExtension,
                        file.absolutePath,
                        totalPages,
                        page,
                        System.currentTimeMillis()
                    )
                )
            } else {
                dao.updateLastViewPage(file.nameWithoutExtension, page)
            }
        }
    }

    fun getUseFile(fileName: String): LiveData<UserFile> {
        return userFileDB.userFileDao().loadFromNameLive(fileName)
    }

    fun getFileProperties(fileName: String): LiveData<FileProperties> {
        return userFileDB.filePropertiesDao().loadFromNameLive(fileName)
    }

    fun isRootPath(currentPath: String): Boolean {
        return currentPath == rootPath || currentPath == cacheDir
    }

    fun getPrevPath(currentPath: String): String {
        return currentPath.substringBeforeLast('/')
    }

    fun getItemPathList(file: File): List<String> {
        file.absolutePath.let { path ->
            val replacePath = when {
                path.contains(rootPath) -> {
                    path.replace(rootPath, "내부저장소")
                }
                path.contains(cacheDir) -> {
                    path.replace(cacheDir, "내파일")
                }
                else -> {
                    path
                }
            }
            return replacePath.split('/')
        }
    }

    private fun getBaseFileDir(file: File): File? {
        if (cacheDir == null)
            return null

        return File(cacheDir, file.nameWithoutExtension).apply {
            mkdirs()
        }
    }

    private fun unzip(zipFile: File): File? {
        return getBaseFileDir(zipFile)?.also { dir ->
            val zip = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                ZipFile(zipFile, Charsets.UTF_8)
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
        }
    }

    private fun separateTextFile(file: File): File? {
        return getBaseFileDir(file)?.also { dir ->
            var entryCount = 0
            var entryLength = 0
            val entries = mutableListOf<String>()
            file.forEachLine { line ->
                if (entryLength >= 300) {
                    entries.add(line)
                    entryLength += line.length
                } else {
                    val destList = entries.toList()
                    entries.clear()
                    entryLength = 0
                    entryCount++
                    val destFilePath = File(dir, entryCount.toString())
                    destFilePath.bufferedWriter().use { bw ->
                        destList.forEach { entry ->
                            bw.write(entry)
                        }
                    }
                }
            }
        }
    }

    enum class FileType(val extensions: List<String> = listOf(), val isSave: Boolean = false) {
        IMG(listOf("png", "jpg", "jpeg", "gif")),
        TXT(listOf("txt"), true),
        ZIP(listOf("zip"), true),
        DIR,
        UNKNOWN;

        companion object {
            @JvmStatic
            fun getType(file: File): FileType {
                return values().firstOrNull {
                    it.extensions.any { extension ->
                        file.extension.endsWith(extension)
                    }
                } ?: if (file.isDirectory) DIR else UNKNOWN
            }
        }
    }
}