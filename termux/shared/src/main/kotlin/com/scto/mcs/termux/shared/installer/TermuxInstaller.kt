package com.scto.mcs.termux.shared.installer

import android.content.Context
import android.system.Os
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TermuxInstaller(private val context: Context) {

    suspend fun installBootstrap(abi: String, progressCallback: (String) -> Unit) = withContext(Dispatchers.IO) {
        val filesDir = context.filesDir
        val usrDir = File(filesDir, "usr")
        
        // 1. Download
        progressCallback("Downloading bootstrap...")
        // Hinweis: Dies ist ein Platzhalter-URL. In einer echten App müsste hier die korrekte URL für die ABI stehen.
        val bootstrapUrl = "https://github.com/termux/termux-packages/files/bootstrap-$abi.zip" 
        val zipFile = File(context.cacheDir, "bootstrap.zip")
        
        URL(bootstrapUrl).openStream().use { input ->
            FileOutputStream(zipFile).use { output ->
                input.copyTo(output)
            }
        }

        // 2. Extract
        progressCallback("Extracting...")
        if (!usrDir.exists()) usrDir.mkdirs()
        
        ZipInputStream(zipFile.inputStream()).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val file = File(filesDir, entry.name)
                if (entry.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile?.mkdirs()
                    FileOutputStream(file).use { fos ->
                        zis.copyTo(fos)
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }

        // 3. Symlink fixing (Beispielhaft)
        progressCallback("Fixing symlinks...")
        val binDir = File(usrDir, "bin")
        val bashFile = File(binDir, "bash")
        val shFile = File(binDir, "sh")
        
        if (bashFile.exists() && !shFile.exists()) {
            try {
                Os.symlink(bashFile.absolutePath, shFile.absolutePath)
            } catch (e: Exception) {
                progressCallback("Error fixing symlinks: ${e.message}")
            }
        }
        
        progressCallback("Installation complete.")
    }
}
