package com.scto.mcs.termux.shared.data

import android.os.Bundle
import com.google.common.base.Strings
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

object DataUtils {

    const val TRANSACTION_SIZE_LIMIT_IN_BYTES = 100 * 1024 // 100KB
    private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

    fun getTruncatedCommandOutput(text: String?, maxLength: Int, fromEnd: Boolean, onNewline: Boolean, addPrefix: Boolean): String? {
        if (text == null) return null

        val prefix = "(truncated) "
        var currentMaxLength = maxLength
        if (addPrefix) currentMaxLength -= prefix.length

        if (currentMaxLength < 0 || text.length < currentMaxLength) return text

        val result = if (fromEnd) {
            text.substring(0, currentMaxLength)
        } else {
            var cutOffIndex = text.length - currentMaxLength
            if (onNewline) {
                val nextNewlineIndex = text.indexOf('\n', cutOffIndex)
                if (nextNewlineIndex != -1 && nextNewlineIndex != text.length - 1) {
                    cutOffIndex = nextNewlineIndex + 1
                }
            }
            text.substring(cutOffIndex)
        }

        return if (addPrefix) prefix + result else result
    }

    fun replaceSubStringsInStringArrayItems(array: Array<String>?, find: String, replace: String) {
        if (array == null || array.isEmpty()) return
        for (i in array.indices) {
            array[i] = array[i].replace(find, replace)
        }
    }

    fun getFloatFromString(value: String?, def: Float): Float {
        return try {
            value?.toFloat() ?: def
        } catch (e: Exception) {
            def
        }
    }

    fun getIntFromString(value: String?, def: Int): Int {
        return try {
            value?.toInt() ?: def
        } catch (e: Exception) {
            def
        }
    }

    fun getStringFromInteger(value: Int?, def: String): String {
        return value?.toString() ?: def
    }

    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = HEX_ARRAY[v ushr 4]
            hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
        }
        return String(hexChars)
    }

    fun getIntStoredAsStringFromBundle(bundle: Bundle?, key: String, def: Int): Int {
        if (bundle == null) return def
        return getIntFromString(bundle.getString(key, def.toString()), def)
    }

    fun clamp(value: Int, min: Int, max: Int): Int {
        return value.coerceIn(min, max)
    }

    fun rangedOrDefault(value: Float, def: Float, min: Float, max: Float): Float {
        return if (value < min || value > max) def else value
    }

    fun getSpaceIndentedString(string: String?, count: Int): String? {
        if (string.isNullOrEmpty()) return string
        return getIndentedString(string, "    ", count)
    }

    fun getTabIndentedString(string: String?, count: Int): String? {
        if (string.isNullOrEmpty()) return string
        return getIndentedString(string, "\t", count)
    }

    fun getIndentedString(string: String, indent: String, count: Int): String {
        if (string.isEmpty()) return string
        return string.replace(Regex("(?m)^"), Strings.repeat(indent, maxOf(count, 1)))
    }

    fun <T> getDefaultIfNull(obj: T?, def: T?): T? {
        return obj ?: def
    }

    fun getDefaultIfUnset(value: String?, def: String): String {
        return if (value.isNullOrEmpty()) def else value
    }

    fun isNullOrEmpty(string: String?): Boolean {
        return string.isNullOrEmpty()
    }

    fun getSerializedSize(obj: Serializable?): Long {
        if (obj == null) return 0
        return try {
            val byteOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteOutputStream)
            objectOutputStream.writeObject(obj)
            objectOutputStream.flush()
            objectOutputStream.close()
            byteOutputStream.toByteArray().size.toLong()
        } catch (e: Exception) {
            -1
        }
    }
}
