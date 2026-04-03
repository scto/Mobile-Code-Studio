package com.scto.mcs.termux.shared.settings.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.scto.mcs.termux.shared.logger.Logger

object SharedPreferenceUtils {

    private const val LOG_TAG = "SharedPreferenceUtils"

    fun getPrivateSharedPreferences(context: Context, name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun getPrivateAndMultiProcessSharedPreferences(context: Context, name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS)
    }

    fun getBoolean(sharedPreferences: SharedPreferences?, key: String, def: Boolean): Boolean {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Error getting boolean value for the \"$key\" key from null shared preferences. Returning default value \"$def\".")
            return def
        }
        return try {
            sharedPreferences.getBoolean(key, def)
        } catch (e: ClassCastException) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error getting boolean value for the \"$key\" key from shared preferences. Returning default value \"$def\".", e)
            def
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setBoolean(sharedPreferences: SharedPreferences?, key: String, value: Boolean, commitToFile: Boolean) {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Ignoring setting boolean value \"$value\" for the \"$key\" key into null shared preferences.")
            return
        }
        val editor = sharedPreferences.edit().putBoolean(key, value)
        if (commitToFile) editor.commit() else editor.apply()
    }

    fun getFloat(sharedPreferences: SharedPreferences?, key: String, def: Float): Float {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Error getting float value for the \"$key\" key from null shared preferences. Returning default value \"$def\".")
            return def
        }
        return try {
            sharedPreferences.getFloat(key, def)
        } catch (e: ClassCastException) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error getting float value for the \"$key\" key from shared preferences. Returning default value \"$def\".", e)
            def
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setFloat(sharedPreferences: SharedPreferences?, key: String, value: Float, commitToFile: Boolean) {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Ignoring setting float value \"$value\" for the \"$key\" key into null shared preferences.")
            return
        }
        val editor = sharedPreferences.edit().putFloat(key, value)
        if (commitToFile) editor.commit() else editor.apply()
    }

    fun getInt(sharedPreferences: SharedPreferences?, key: String, def: Int): Int {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Error getting int value for the \"$key\" key from null shared preferences. Returning default value \"$def\".")
            return def
        }
        return try {
            sharedPreferences.getInt(key, def)
        } catch (e: ClassCastException) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error getting int value for the \"$key\" key from shared preferences. Returning default value \"$def\".", e)
            def
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setInt(sharedPreferences: SharedPreferences?, key: String, value: Int, commitToFile: Boolean) {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Ignoring setting int value \"$value\" for the \"$key\" key into null shared preferences.")
            return
        }
        val editor = sharedPreferences.edit().putInt(key, value)
        if (commitToFile) editor.commit() else editor.apply()
    }

    @SuppressLint("ApplySharedPref")
    fun getAndIncrementInt(sharedPreferences: SharedPreferences?, key: String, def: Int, commitToFile: Boolean, resetValue: Int?): Int {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Ignoring incrementing int value for the \"$key\" key into null shared preferences.")
            return def
        }
        var curValue = getInt(sharedPreferences, key, def)
        if (resetValue != null && curValue < 0) curValue = resetValue
        var newValue = curValue + 1
        if (resetValue != null && newValue < 0) newValue = resetValue
        setInt(sharedPreferences, key, newValue, commitToFile)
        return curValue
    }

    fun getLong(sharedPreferences: SharedPreferences?, key: String, def: Long): Long {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Error getting long value for the \"$key\" key from null shared preferences. Returning default value \"$def\".")
            return def
        }
        return try {
            sharedPreferences.getLong(key, def)
        } catch (e: ClassCastException) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error getting long value for the \"$key\" key from shared preferences. Returning default value \"$def\".", e)
            def
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setLong(sharedPreferences: SharedPreferences?, key: String, value: Long, commitToFile: Boolean) {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Ignoring setting long value \"$value\" for the \"$key\" key into null shared preferences.")
            return
        }
        val editor = sharedPreferences.edit().putLong(key, value)
        if (commitToFile) editor.commit() else editor.apply()
    }

    fun getString(sharedPreferences: SharedPreferences?, key: String, def: String?, defIfEmpty: Boolean): String? {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Error getting String value for the \"$key\" key from null shared preferences. Returning default value \"$def\".")
            return def
        }
        return try {
            val value = sharedPreferences.getString(key, def)
            if (defIfEmpty && value.isNullOrEmpty()) def else value
        } catch (e: ClassCastException) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error getting String value for the \"$key\" key from shared preferences. Returning default value \"$def\".", e)
            def
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setString(sharedPreferences: SharedPreferences?, key: String, value: String?, commitToFile: Boolean) {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Ignoring setting String value \"$value\" for the \"$key\" key into null shared preferences.")
            return
        }
        val editor = sharedPreferences.edit().putString(key, value)
        if (commitToFile) editor.commit() else editor.apply()
    }

    fun getStringSet(sharedPreferences: SharedPreferences?, key: String, def: Set<String>?): Set<String>? {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Error getting Set<String> value for the \"$key\" key from null shared preferences. Returning default value \"$def\".")
            return def
        }
        return try {
            sharedPreferences.getStringSet(key, def)
        } catch (e: ClassCastException) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Error getting Set<String> value for the \"$key\" key from shared preferences. Returning default value \"$def\".", e)
            def
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setStringSet(sharedPreferences: SharedPreferences?, key: String, value: Set<String>?, commitToFile: Boolean) {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Ignoring setting Set<String> value \"$value\" for the \"$key\" key into null shared preferences.")
            return
        }
        val editor = sharedPreferences.edit().putStringSet(key, value)
        if (commitToFile) editor.commit() else editor.apply()
    }

    fun getIntStoredAsString(sharedPreferences: SharedPreferences?, key: String, def: Int): Int {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Error getting int value for the \"$key\" key from null shared preferences. Returning default value \"$def\".")
            return def
        }
        return try {
            val stringValue = sharedPreferences.getString(key, def.toString())
            stringValue?.toInt() ?: def
        } catch (e: Exception) {
            when (e) {
                is NumberFormatException, is ClassCastException -> def
                else -> throw e
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    fun setIntStoredAsString(sharedPreferences: SharedPreferences?, key: String, value: Int, commitToFile: Boolean) {
        if (sharedPreferences == null) {
            Logger.logError(LOG_TAG, "Ignoring setting int value \"$value\" for the \"$key\" key into null shared preferences.")
            return
        }
        val editor = sharedPreferences.edit().putString(key, value.toString())
        if (commitToFile) editor.commit() else editor.apply()
    }
}
