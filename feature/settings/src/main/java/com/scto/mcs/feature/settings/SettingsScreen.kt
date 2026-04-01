package com.scto.mcs.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item { SettingsCategory("General") }
            item { SettingsItem("Theme", uiState.theme) }
            item { SettingsItem("Language", "English") }

            item { SettingsCategory("Editor") }
            item { SettingsItem("Font Size", uiState.fontSize.toString()) }
            item { SettingsItem("Tab Width", "4") }
            item { SettingsItem("Autocomplete", "Enabled") }

            item { SettingsCategory("Terminal") }
            item { SettingsItem("Shell Path", uiState.shellPath) }
            item { SettingsItem("Cursor Blinking", "Enabled") }

            item { SettingsCategory("Build") }
            item { SettingsItem("JDK Path", uiState.jdkPath) }
            item { SettingsItem("Gradle Daemon", "Enabled") }

            item { SettingsCategory("About") }
            item { SettingsItem("Version", "0.0.1") }
            item { SettingsItem("Licenses", "JGit, Sora-Editor") }
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(title: String, subtitle: String) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) }
    )
}
