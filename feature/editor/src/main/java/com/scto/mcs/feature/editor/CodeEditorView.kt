package com.scto.mcs.feature.editor

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.widget.CodeEditor

@Composable
fun CodeEditorView(
    modifier: Modifier = Modifier,
    onEditorCreated: (CodeEditor) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CodeEditor(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                onEditorCreated(this)
            }
        }
    )
}
