package com.scto.mcs.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.ui.components.MCSToolbar

@Composable
fun TermuxSettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MCSToolbar(title = "Terminal Einstellungen")
        
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Schriftgröße: ${viewModel.fontSize}", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = viewModel.fontSize.toFloat(),
                onValueChange = { viewModel.setFontSize(it.toInt()) },
                valueRange = 8f..32f
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Farbschema: ${viewModel.colorScheme}", style = MaterialTheme.typography.bodyLarge)
            // Hier könnte ein Dropdown für Farbschemata folgen
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { viewModel.resetEnvironment() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Umgebung zurücksetzen")
            }
        }
    }
}
