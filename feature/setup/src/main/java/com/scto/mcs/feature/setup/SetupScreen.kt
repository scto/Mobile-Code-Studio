package com.scto.mcs.feature.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scto.mcs.core.BootstrapManager

@Composable
fun SetupScreen(
    bootstrapManager: BootstrapManager,
    onSetupComplete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(!bootstrapManager.isEnvironmentSetup()) }
    var selectedJdk by remember { mutableStateOf(17) }
    var selectedSdk by remember { mutableStateOf(34) }
    var logs by remember { mutableStateOf("Waiting for setup...") }
    val scrollState = rememberScrollState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Environment Setup") },
            text = {
                Column {
                    Text("Select JDK Version:")
                    Row {
                        RadioButton(selected = selectedJdk == 17, onClick = { selectedJdk = 17 })
                        Text("17")
                        RadioButton(selected = selectedJdk == 21, onClick = { selectedJdk = 21 })
                        Text("21")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select Android SDK Version:")
                    // Simplified selection
                    Row {
                        listOf(33, 34, 35, 36).forEach { sdk ->
                            FilterChip(
                                selected = selectedSdk == sdk,
                                onClick = { selectedSdk = sdk },
                                label = { Text(sdk.toString()) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    bootstrapManager.startBootstrap(selectedJdk, selectedSdk, { log ->
                        logs += "\n$log"
                    }, {
                        onSetupComplete()
                    })
                }) { Text("Start Bootstrap") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Terminal Output:", style = MaterialTheme.typography.titleMedium)
        Text(
            logs, 
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        )
    }
}
