package com.kobbi.project.zipviewer.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "FileProperties",
    primaryKeys = ["name"],
    indices = [Index(value = ["lastOpenTime"], unique = false)],
    foreignKeys = [ForeignKey(
        entity = UserFile::class,
        parentColumns = ["filePath"],
        childColumns = ["path"]
    )]
)
data class FileProperties(
    val name: String,
    val path: String,
    val totalPage: Int,
    val lastViewPage: Int,
    val lastOpenTime: Long
)