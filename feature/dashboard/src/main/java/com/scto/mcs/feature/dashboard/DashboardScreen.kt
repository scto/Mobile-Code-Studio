package com.scto.mcs.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.ui.components.MCSIcons
import com.scto.mcs.core.ui.components.MCSToolbar

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onProjectClick: (String) -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    var showCloneDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { MCSToolbar(title = "Projekte") },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCloneDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Projekt hinzufügen")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(projects) { project ->
                ListItem(
                    headlineContent = { Text(project.name) },
                    leadingContent = { Icon(MCSIcons.Folder, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (showCloneDialog) {
            CloneProjectDialog(
                onDismiss = { showCloneDialog = false },
                onConfirm = { url, dest ->
                    viewModel.cloneProject(url, dest)
                    showCloneDialog = false
                }
            )
        }
    }
}
