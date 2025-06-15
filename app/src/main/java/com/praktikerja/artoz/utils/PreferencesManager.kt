package com.praktikerja.artoz.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "book_prefs"
        private const val KEY_LAYOUT = "isStaggedGridLayout"
    }

    fun setLayoutGrid(isGrid: Boolean) {
        prefs.edit().putBoolean(KEY_LAYOUT, isGrid).apply()
    }

    fun isLayoutGrid(): Boolean {
        return prefs.getBoolean(KEY_LAYOUT, false)
    }
}
