package com.scto.mcs.feature.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.rosemoe.sora.widget.CodeEditor
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    file: File,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val content by viewModel.fileContent.collectAsState()
    val isDirty by viewModel.isDirty.collectAsState()
    val buildOutput by viewModel.buildOutput.collectAsState(initial = "")
    var editorInstance by remember { mutableStateOf<CodeEditor?>(null) }
    var showBuildOutput by remember { mutableStateOf(false) }
    val buildLogs = remember { mutableStateListOf<String>() }

    LaunchedEffect(file) {
        viewModel.loadFile(file)
    }

    LaunchedEffect(buildOutput) {
        if (buildOutput.isNotEmpty()) {
            buildLogs.add(buildOutput)
            showBuildOutput = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(file.name + if (isDirty) "*" else "") },
                actions = {
                    IconButton(onClick = { viewModel.buildProject(file.parentFile!!) }) {
                        Icon(Icons.Default.Build, contentDescription = "Build")
                    }
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

    if (showBuildOutput) {
        ModalBottomSheet(onDismissRequest = { showBuildOutput = false }) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(buildLogs) { log ->
                    Text(log, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
