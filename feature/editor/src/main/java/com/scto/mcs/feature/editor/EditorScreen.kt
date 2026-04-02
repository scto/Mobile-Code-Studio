package com.scto.mcs.feature.editor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun EditorScreen(engine: EditorEngine) {
    val code by engine.codeContent.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        TextField(
            value = code,
            onValueChange = { engine.updateCode(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = { Text("Code Editor") }
        )
    }
}
