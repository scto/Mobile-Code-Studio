package com.scto.mcs.core

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

/**
 * I/O Benchmark Suite: Messung der Schreibgeschwindigkeiten und "Links per Second".
 */
@Singleton
class IOBenchmarkSuite @Inject constructor(
    private val nativeBridge: NativeBridge
) {

    data class BenchmarkResult(
        val operation: String,
        val durationMs: Long,
        val itemsProcessed: Int,
        val itemsPerSecond: Double
    )

    /**
     * Benchmark für die Erstellung von symbolischen Links.
     * @param testDir Verzeichnis für den Test.
     * @param count Anzahl der zu erstellenden Links.
     * @return BenchmarkResult.
     */
    fun benchmarkSymlinkCreation(testDir: File, count: Int): BenchmarkResult {
        val targetFile = File(testDir, "benchmark_target")
        targetFile.writeText("benchmark")
        
        val time = measureTimeMillis {
            for (i in 0 until count) {
                val linkFile = File(testDir, "link_$i")
                nativeBridge.createSymlink(targetFile.absolutePath, linkFile.absolutePath)
            }
        }

        // Cleanup
        targetFile.delete()
        for (i in 0 until count) {
            File(testDir, "link_$i").delete()
        }

        val itemsPerSecond = if (time > 0) (count.toDouble() / (time.toDouble() / 1000.0)) else 0.0
        return BenchmarkResult("Symlink Creation", time, count, itemsPerSecond)
    }

    /**
     * Benchmark für einfache Schreiboperationen.
     * @param testDir Verzeichnis für den Test.
     * @param count Anzahl der Schreibvorgänge.
     * @return BenchmarkResult.
     */
    fun benchmarkWriteSpeed(testDir: File, count: Int): BenchmarkResult {
        val time = measureTimeMillis {
            for (i in 0 until count) {
                val file = File(testDir, "write_test_$i")
                file.writeText("benchmark data")
                file.delete()
            }
        }

        val itemsPerSecond = if (time > 0) (count.toDouble() / (time.toDouble() / 1000.0)) else 0.0
        return BenchmarkResult("Write Speed", time, count, itemsPerSecond)
    }
}
