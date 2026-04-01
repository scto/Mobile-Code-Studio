package com.scto.mcs.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scto.mcs.core.model.ProjectConfig

@Composable
fun ProjectConfigDialog(
    templateType: String,
    onDismiss: () -> Unit,
    onConfirm: (ProjectConfig) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("com.example.myapp") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure $templateType") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Project Name") })
                TextField(value = packageName, onValueChange = { packageName = it }, label = { Text("Package Name") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(ProjectConfig(name, packageName, "/sdcard/mobilecodestudio/$name", templateType)) }) {
                Text("Create")
            }
        }
    )
}
