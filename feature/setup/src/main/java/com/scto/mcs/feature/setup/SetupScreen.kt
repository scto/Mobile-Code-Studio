package com.scto.mcs.feature.setup

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.scto.mcs.core.BootstrapManager
import com.scto.mcs.core.TerminalSessionManager
import com.termux.view.TerminalView

@Composable
fun SetupScreen(
    bootstrapManager: BootstrapManager,
    terminalSessionManager: TerminalSessionManager,
    onSetupComplete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(!bootstrapManager.isEnvironmentSetup()) }
    var selectedJdk by remember { mutableStateOf(17) }
    var selectedSdk by remember { mutableStateOf(34) }
    
    LaunchedEffect(Unit) {
        terminalSessionManager.startSession()
    }

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
                        terminalSessionManager.execute("echo '$log'")
                    }, {
                        onSetupComplete()
                    })
                }) { Text("Start Bootstrap") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Terminal Output:", style = MaterialTheme.typography.titleMedium)
        AndroidView(
            modifier = Modifier.fillMaxWidth().weight(1f),
            factory = { context ->
                TerminalView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )
    }
}
