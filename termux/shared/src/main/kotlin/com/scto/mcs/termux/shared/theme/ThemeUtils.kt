package com.scto.mcs.termux.shared.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.tom.rv2ide.preferences.internal.GeneralPreferences
import com.tom.rv2ide.utils.isSystemInDarkMode

object ThemeUtils {

    const val ATTR_TEXT_COLOR_PRIMARY = android.R.attr.textColorPrimary
    const val ATTR_TEXT_COLOR_SECONDARY = android.R.attr.textColorSecondary
    const val ATTR_TEXT_COLOR = android.R.attr.textColor
    const val ATTR_TEXT_COLOR_LINK = android.R.attr.textColorLink

    fun isNightModeEnabled(context: Context?): Boolean {
        return context?.let { isSystemInDarkMode(it) } ?: false
    }

    fun shouldEnableDarkTheme(context: Context, name: String): Boolean {
        return GeneralPreferences.uiMode == AppCompatDelegate.getDefaultNightMode() || isNightModeEnabled(context)
    }

    fun getTextColorPrimary(context: Context): Int = getSystemAttrColor(context, ATTR_TEXT_COLOR_PRIMARY)
    fun getTextColorSecondary(context: Context): Int = getSystemAttrColor(context, ATTR_TEXT_COLOR_SECONDARY)
    fun getTextColor(context: Context): Int = getSystemAttrColor(context, ATTR_TEXT_COLOR)
    fun getTextColorLink(context: Context): Int = getSystemAttrColor(context, ATTR_TEXT_COLOR_LINK)

    fun getSystemAttrColor(context: Context, attr: Int): Int = getSystemAttrColor(context, attr, 0)

    fun getSystemAttrColor(context: Context, attr: Int, def: Int): Int {
        val typedArray = context.theme.obtainStyledAttributes(intArrayOf(attr))
        val color = typedArray.getColor(0, def)
        typedArray.recycle()
        return color
    }
}
