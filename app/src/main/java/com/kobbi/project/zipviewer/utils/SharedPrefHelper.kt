package com.kobbi.project.zipviewer.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper private constructor() {
    companion object {
        private const val KEY_PREFERENCE = "_Preference"

        const val KEY_LAST_VIEW_FILE_PAGE_NAME = "last_view_file_name"
        const val KEY_LAST_OPEN_FILE_PATH = "lat_open_file_path"

        @JvmStatic
        fun getPreference(context: Context): SharedPreferences {
            return context.applicationContext.getSharedPreferences(
                "${context.packageName}${KEY_PREFERENCE}", Context.MODE_PRIVATE
            )
        }

        @JvmStatic
        fun setBool(context: Context, key: String, value: Boolean) {
            getPreference(context).edit().run {
                putBoolean(key, value)
                apply()
            }
        }

        @JvmStatic
        fun getBool(context: Context, key: String, defValue: Boolean = false): Boolean {
            return getPreference(context).getBoolean(key, defValue)
        }

        fun setLong(context: Context, key: String, value: Long) {
            getPreference(context).edit().run {
                putLong(key, value)
                apply()
            }
        }

        fun getLong(context: Context, key: String, defValue: Long = 0L): Long {
            return getPreference(context).getLong(key, defValue)
        }

        fun setString(context: Context, key: String, value: String) {
            getPreference(context).edit().run {
                putString(key, value)
                apply()
            }
        }

        fun getString(context: Context, key: String, defValue: String = "") =
            getPreference(context).getString(key, defValue) ?: defValue
    }
}