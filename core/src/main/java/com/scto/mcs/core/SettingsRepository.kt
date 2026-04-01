package com.scto.mcs.core

import android.content.Context

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("mcs_settings", Context.MODE_PRIVATE)

    fun getString(key: String, default: String): String = prefs.getString(key, default) ?: default
    fun setString(key: String, value: String) = prefs.edit().putString(key, value).apply()

    fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)
    fun setInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()

    fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    fun setBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()
}
