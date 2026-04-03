package com.scto.mcs.termux.shared.settings.preferences

import android.content.Context
import android.content.SharedPreferences

open class AppSharedPreferences(
    val context: Context,
    val sharedPreferences: SharedPreferences?,
    val multiProcessSharedPreferences: SharedPreferences? = null
)
