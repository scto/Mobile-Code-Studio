package com.scto.mcs.feature.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.ui.components.MCSToolbar
import com.scto.mcs.core.ui.components.ProgressDashboard
import com.scto.mcs.core.ui.components.InstallPhase

@Composable
fun SetupScreen(viewModel: SetupViewModel = hiltViewModel()) {
    var showJdkDialog by remember { mutableStateOf(false) }
    var showBuildToolsDialog by remember { mutableStateOf(false) }
    val progressState by viewModel.progressState.collectAsState()
    val jdk by viewModel.jdkVersion.collectAsState()
    val buildTools by viewModel.buildToolsVersion.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        MCSToolbar(title = "Konfiguration")
        
        if (progressState.phase == InstallPhase.IDLE) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Wähle die Umgebungseinstellungen:", style = MaterialTheme.typography.titleMedium)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(onClick = { showJdkDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("JDK Version: $jdk")
                }
                
                OutlinedButton(onClick = { showBuildToolsDialog = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Build-Tools: $buildTools")
                }

                Spacer(modifier = Modifier.weight(1f))
                
                Button(onClick = { viewModel.startInstallation() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Installation starten")
                }
            }
        } else {
            ProgressDashboard(state = progressState)
        }
    }

    if (showJdkDialog) {
        AlertDialog(
            onDismissRequest = { showJdkDialog = false },
            title = { Text("JDK wählen") },
            text = {
                Column {
                    listOf("openjdk-17", "openjdk-21").forEach { ver ->
                        TextButton(onClick = { viewModel.setJdkVersion(ver); showJdkDialog = false }) {
                            Text(ver)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showJdkDialog = false }) { Text("Abbrechen") } }
        )
    }

    if (showBuildToolsDialog) {
        AlertDialog(
            onDismissRequest = { showBuildToolsDialog = false },
            title = { Text("Build-Tools wählen") },
            text = {
                Column {
                    listOf("33", "34", "35", "36").forEach { ver ->
                        TextButton(onClick = { viewModel.setBuildToolsVersion(ver); showBuildToolsDialog = false }) {
                            Text(ver)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showBuildToolsDialog = false }) { Text("Abbrechen") } }
        )
    }
}
