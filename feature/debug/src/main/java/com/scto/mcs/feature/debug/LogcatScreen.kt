package com.scto.mcs.feature.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogcatScreen(viewModel: LogcatViewModel = hiltViewModel()) {
    val logs by viewModel.logs.collectAsState()
    var filter by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logcat") },
                actions = {
                    IconButton(onClick = { viewModel.startLogging() }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                    }
                    IconButton(onClick = { viewModel.stopLogging() }) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop")
                    }
                    IconButton(onClick = { viewModel.clearLogs() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = filter,
                onValueChange = { filter = it },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                placeholder = { Text("Filter logs...") }
            )
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(logs.filter { it.message.contains(filter, ignoreCase = true) }) { log ->
                    LogItem(log)
                }
            }
        }
    }
}

@Composable
fun LogItem(log: LogEntry) {
    val color = when (log.level) {
        "E" -> Color.Red
        "W" -> Color.Yellow
        "I" -> Color.Green
        "D" -> Color.Blue
        else -> Color.Gray
    }
    
    Row(modifier = Modifier.padding(8.dp)) {
        Text(log.level, color = color, modifier = Modifier.width(30.dp))
        Text(log.message)
    }
}
