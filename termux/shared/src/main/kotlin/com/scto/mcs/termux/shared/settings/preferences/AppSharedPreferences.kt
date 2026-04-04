package com.scto.mcs.termux.shared.settings.preferences

import android.content.Context
import android.content.SharedPreferences

/** A class that holds [SharedPreferences] objects for apps. */
open class AppSharedPreferences(
    val context: Context,
    val sharedPreferences: SharedPreferences?,
    val multiProcessSharedPreferences: SharedPreferences? = null
)
