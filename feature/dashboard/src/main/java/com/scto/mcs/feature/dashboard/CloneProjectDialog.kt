package com.scto.mcs.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CloneProjectDialog(
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    var url by remember { mutableStateOf("") }
    var targetDir by remember { mutableStateOf("/sdcard/mobilecodestudio/my-project") }
    val cloneState by viewModel.cloneState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clone Repository") },
        text = {
            Column {
                TextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Repository URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = targetDir,
                    onValueChange = { targetDir = it },
                    label = { Text("Target Directory") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                when (val state = cloneState) {
                    is CloneState.Cloning -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { state.progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is CloneState.Error -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.cloneRepository(url, targetDir) },
                enabled = cloneState !is CloneState.Cloning
            ) {
                Text("Clone")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
