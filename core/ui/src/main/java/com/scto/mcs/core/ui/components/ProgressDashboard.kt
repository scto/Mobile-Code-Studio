package com.scto.mcs.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class InstallPhase {
    IDLE, DOWNLOAD, EXTRACTION, LINKING, COMPLETED
}

data class ProgressState(
    val phase: InstallPhase,
    val progress: Float, // 0.0 to 1.0
    val message: String
)

@Composable
fun ProgressDashboard(
    state: ProgressState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Phase: ${state.phase.name}",
            style = MaterialTheme.typography.titleMedium
        )
        
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.fillMaxWidth(),
        )
        
        Text(
            text = state.message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
