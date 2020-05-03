package com.kobbi.project.zipviewer.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kobbi.project.zipviewer.data.database.entity.UserFile

@Dao
interface UserFileDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg userFile: UserFile)

    @Delete
    fun delete(vararg userFile: UserFile)

    @Query("SELECT * FROM UserFile")
    fun loadAll(): List<UserFile>

    @Query("SELECT * FROM UserFile")
    fun loadAllLive(): LiveData<List<UserFile>>

    @Query("SELECT * FROM UserFile WHERE filePath = :filePath")
    fun loadFromPath(filePath: String): UserFile?

    @Query("SELECT * FROM UserFile WHERE filePath = :filePath")
    fun loadFromPathLive(filePath: String): LiveData<UserFile>

    @Query("SELECT * FROM UserFile WHERE fileName = :fileName")
    fun loadFromName(fileName: String): UserFile?

    @Query("SELECT * FROM UserFile WHERE fileName = :fileName")
    fun loadFromNameLive(fileName: String): LiveData<UserFile>
}