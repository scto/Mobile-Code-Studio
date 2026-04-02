package com.scto.mcs.core

import android.content.Context
import android.os.Build
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Prüft, ob die Berechtigung für den Zugriff auf alle Dateien (Android 11+) vorhanden ist.
     */
    fun hasManageExternalStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true // Nicht erforderlich für ältere Versionen
        }
    }

    /**
     * Prüft, ob in ein spezifisches Verzeichnis geschrieben werden kann.
     */
    fun canWriteToPath(path: String): Boolean {
        val file = File(path)
        // Versuche, eine temporäre Datei zu erstellen, um Schreibrechte zu verifizieren
        return try {
            if (!file.exists()) file.mkdirs()
            val testFile = File(file, ".write_test")
            val success = testFile.createNewFile()
            if (success) testFile.delete()
            success
        } catch (e: Exception) {
            false
        }
    }
}
