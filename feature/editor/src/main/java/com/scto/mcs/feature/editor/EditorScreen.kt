package com.scto.mcs.feature.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    file: File,
    viewModel: EditorViewModel = viewModel()
) {
    val content by viewModel.fileContent.collectAsState()
    var editorInstance by remember { mutableStateOf<io.github.rosemoe.sora.widget.CodeEditor?>(null) }

    LaunchedEffect(file) {
        viewModel.loadFile(file)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(file.name) },
                actions = {
                    IconButton(onClick = {
                        editorInstance?.let {
                            viewModel.saveFile(file, it.text.toString())
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CodeEditorView(
                modifier = Modifier.fillMaxSize()
            ) { editor ->
                editorInstance = editor
                editor.setText(content)
            }
        }
    }
}
