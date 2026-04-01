package com.scto.mcs.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item { SettingsCategory("General") }
            item { SettingsItem("Theme", "Dark Mode") }
            item { SettingsCategory("Editor") }
            item { SettingsItem("Font Size", "14sp") }
            item { SettingsCategory("Terminal") }
            item { SettingsItem("Shell", "/system/bin/sh") }
            item { SettingsCategory("Build") }
            item { SettingsItem("Build Output", "/sdcard/build") }
            item { SettingsCategory("Debug") }
            item { SettingsItem("Log Level", "Verbose") }
            item { SettingsCategory("About") }
            item { SettingsItem("Version", "0.0.1") }
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
