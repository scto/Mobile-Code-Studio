package com.scto.mcs.feature.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.termux.shared.installer.TermuxInstaller
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(
    viewModel: SetupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Bereit zur Installation") }
    var isInstalling by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Termux Setup", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = status)
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                isInstalling = true
                scope.launch {
                    val installer = TermuxInstaller(context)
                    installer.installBootstrap("aarch64") { progress ->
                        status = progress
                    }
                    isInstalling = false
                }
            },
            enabled = !isInstalling
        ) {
            Text(if (isInstalling) "Installiere..." else "Installation starten")
        }
    }
}
