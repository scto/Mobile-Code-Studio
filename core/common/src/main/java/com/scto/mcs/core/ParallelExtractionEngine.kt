package com.scto.mcs.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Engine zur parallelen Verarbeitung von Extraktions- und Linking-Aufgaben.
 * Nutzt Kotlin Channels, um Aufgaben an eine Gruppe von Worker-Coroutines zu verteilen.
 */
@Singleton
class ParallelExtractionEngine @Inject constructor(
    private val nativeBridge: NativeBridge
) {
    private val extractionChannel = Channel<ExtractionTask>(Channel.BUFFERED)

    data class ExtractionTask(
        val target: String,
        val linkPath: String
    )

    /**
     * Startet die Worker-Pool-Verarbeitung.
     * @param scope Der CoroutineScope, in dem die Worker laufen sollen.
     * @param workerCount Anzahl der parallelen Worker (Standard: 4).
     */
    fun start(scope: CoroutineScope, workerCount: Int = 4) {
        repeat(workerCount) {
            scope.launch(Dispatchers.IO) {
                for (task in extractionChannel) {
                    nativeBridge.createSymlink(task.target, task.linkPath)
                }
            }
        }
    }

    /**
     * Fügt eine Aufgabe zur Warteschlange hinzu.
     */
    suspend fun submitTask(task: ExtractionTask) {
        extractionChannel.send(task)
    }

    /**
     * Schließt den Channel und beendet die Verarbeitung.
     */
    fun close() {
        extractionChannel.close()
    }
}
