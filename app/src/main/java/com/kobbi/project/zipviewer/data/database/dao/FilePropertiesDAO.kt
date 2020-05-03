package com.kobbi.project.zipviewer.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kobbi.project.zipviewer.data.database.entity.FileProperties

@Dao
interface FilePropertiesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg fileProperties: FileProperties)

    @Delete
    fun delete(vararg fileProperties: FileProperties)

    @Query("UPDATE FileProperties SET lastViewPage = :page, lastOpenTime=:time WHERE name = :name")
    fun updateLastViewPage(name: String, page: Int, time: Long = System.currentTimeMillis())

    @Query("SELECT * FROM FileProperties")
    fun loadAll(): List<FileProperties>

    @Query("SELECT * FROM FileProperties")
    fun loadAllLive(): LiveData<List<FileProperties>>

    @Query("SELECT * FROM FileProperties WHERE name = :fileName")
    fun loadFromName(fileName: String): FileProperties?

    @Query("SELECT * FROM FileProperties WHERE name = :fileName")
    fun loadFromNameLive(fileName: String): LiveData<FileProperties>
}