package com.scto.mcs.feature.editor

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.ui.components.MCSToolbar
import com.scto.mcs.core.ui.components.TerminalText
import io.github.jksys.lib.editor.CodeEditor

@Composable
fun EditorScreen(
    viewModel: EditorViewModel = hiltViewModel(),
    onBuildClick: () -> Unit
) {
    val code by viewModel.code.collectAsState()
    val buildOutput by viewModel.buildOutput.collectAsState()

    Scaffold(
        topBar = { MCSToolbar(title = "Editor") },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth().height(150.dp).padding(8.dp)) {
                Button(onClick = onBuildClick) { Text("Build") }
                TerminalText(text = buildOutput, modifier = Modifier.fillMaxSize())
            }
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(padding),
            factory = { context ->
                CodeEditor(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setText(code)
                }
            },
            update = { editor ->
                if (editor.text.toString() != code) {
                    editor.setText(code)
                }
            }
        )
    }
}
