package com.scto.mcs.feature.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.ui.components.MCSToolbar
import com.scto.mcs.core.ui.components.TerminalText

@Composable
fun SetupScreen(viewModel: SetupViewModel = hiltViewModel()) {
    val progress by viewModel.progress.collectAsState()
    val logs by viewModel.logs.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        MCSToolbar(title = "Terminal Setup")
        
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { viewModel.startSetup() }) {
                Text("Installation starten")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TerminalText(text = logs)
        }
    }
}
