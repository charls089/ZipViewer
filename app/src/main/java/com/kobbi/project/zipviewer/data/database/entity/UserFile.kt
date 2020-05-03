package com.kobbi.project.zipviewer.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "UserFile",
    primaryKeys = ["filePath"],
    indices = [Index(value = ["fileName", "filePath"], unique = false)]
)
data class UserFile(
    val fileName: String,
    val filePath: String,
    val type: String
)