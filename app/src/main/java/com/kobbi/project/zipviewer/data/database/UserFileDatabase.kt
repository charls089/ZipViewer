package com.kobbi.project.zipviewer.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kobbi.project.zipviewer.data.database.dao.FilePropertiesDAO
import com.kobbi.project.zipviewer.data.database.dao.UserFileDAO
import com.kobbi.project.zipviewer.data.database.entity.FileProperties
import com.kobbi.project.zipviewer.data.database.entity.UserFile

@Database(
    entities = [UserFile::class, FileProperties::class],
    version = 1
)
abstract class UserFileDatabase : RoomDatabase() {
    abstract fun userFileDao(): UserFileDAO
    abstract fun filePropertiesDao(): FilePropertiesDAO

    companion object {
        private const val DB_NAME = "UserFileDatabase.db"

        private var INSTANCE: UserFileDatabase? = null
        private var mListener: Listener? = null

        @JvmStatic
        fun getDatabase(context: Context): UserFileDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    UserFileDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            mListener?.onCreate()
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            mListener?.onOpen()
                        }
                    })
                    .build().also { db ->
                        INSTANCE = db
                    }
            }
        }

        @JvmStatic
        fun setListener(listener: Listener) {
            mListener = listener
        }
    }

    interface Listener {
        fun onCreate()
        fun onOpen()
    }
}