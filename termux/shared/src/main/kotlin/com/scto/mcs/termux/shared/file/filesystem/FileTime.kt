package com.scto.mcs.termux.shared.file.filesystem

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Objects
import java.util.concurrent.TimeUnit

class FileTime private constructor(private val value: Long, private val unit: TimeUnit) {

    companion object {
        fun from(value: Long, unit: TimeUnit): FileTime {
            Objects.requireNonNull(unit, "unit")
            return FileTime(value, unit)
        }

        fun fromMillis(value: Long): FileTime {
            return FileTime(value, TimeUnit.MILLISECONDS)
        }

        fun getDate(milliSeconds: Long, format: String): String {
            return try {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = milliSeconds
                SimpleDateFormat(format).format(calendar.time)
            } catch (e: Exception) {
                milliSeconds.toString()
            }
        }
    }

    fun to(unit: TimeUnit): Long {
        Objects.requireNonNull(unit, "unit")
        return unit.convert(this.value, this.unit)
    }

    fun toMillis(): Long {
        return unit.toMillis(value)
    }

    override fun toString(): String {
        return getDate(toMillis(), "yyyy.MM.dd HH:mm:ss.SSS z")
    }
}
