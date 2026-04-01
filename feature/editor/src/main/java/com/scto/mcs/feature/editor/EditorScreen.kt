package com.scto.mcs.feature.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.rosemoe.sora.widget.CodeEditor
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    file: File,
    viewModel: EditorViewModel = viewModel()
) {
    val content by viewModel.fileContent.collectAsState()
    val isDirty by viewModel.isDirty.collectAsState()
    var editorInstance by remember { mutableStateOf<CodeEditor?>(null) }

    LaunchedEffect(file) {
        viewModel.loadFile(file)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(file.name + if (isDirty) "*" else "") },
                actions = {
                    IconButton(
                        onClick = {
                            editorInstance?.let {
                                viewModel.saveFile(file, it.text.toString())
                            }
                        },
                        enabled = isDirty
                    ) {
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
                
                // Configure Editor
                editor.isOverScrollEnabled = true
                editor.isLineNumberEnabled = true
                editor.isEdgeEnabled = true
                
                val configManager = viewModel.getConfigManager()
                editor.setEditorLanguage(configManager.getLanguageForExtension(file.extension))
                editor.setColorScheme(configManager.getEditorColorScheme())
                
                editor.setOnTextChangedListener { _, _, _, _, _ ->
                    viewModel.onTextChanged(editor.text.toString())
                }
            }
        }
    }
}
