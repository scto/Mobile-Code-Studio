package com.scto.mcs.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CloneProjectDialog(
    viewModel: CloneViewModel,
    onDismiss: () -> Unit
) {
    var url by remember { mutableStateOf("") }
    var targetDir by remember { mutableStateOf("/sdcard/mobilecodestudio/my-project") }
    val uiState by viewModel.uiState.collectAsState()

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
                
                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text(uiState.progress, style = MaterialTheme.typography.bodySmall)
                }
                
                uiState.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.cloneRepository(url, targetDir) },
                enabled = !uiState.isLoading
            ) {
                Text("Clone")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
