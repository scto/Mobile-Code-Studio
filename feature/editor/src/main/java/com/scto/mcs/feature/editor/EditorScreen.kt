package com.scto.mcs.feature.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.ui.components.MCSToolbar
import com.scto.mcs.core.ui.components.TerminalText
import java.io.File

@Composable
fun EditorScreen(
    viewModel: EditorViewModel = hiltViewModel(),
    file: File
) {
    var code by remember { mutableStateOf("") }
    val buildOutput by viewModel.buildOutput.collectAsState()

    LaunchedEffect(file) {
        viewModel.loadFile(file)
        code = viewModel.code.value
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MCSToolbar(title = file.name)
        
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            modifier = Modifier.weight(1f).fillMaxWidth(),
            label = { Text("Code") }
        )
        
        Button(
            onClick = { 
                viewModel.saveFile(file, code)
                viewModel.runBuild(file.parentFile!!) 
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Build")
        }
        
        TerminalText(text = buildOutput, modifier = Modifier.height(150.dp).fillMaxWidth().padding(8.dp))
    }
}
