package com.scto.mcs.feature.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry

@Composable
fun EditorComponent(
    modifier: Modifier = Modifier,
    content: String,
    onContentChange: (String) -> Unit,
    language: io.github.rosemoe.sora.langs.textmate.TextMateLanguage
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CodeEditor(context).apply {
                setEditorLanguage(language)
                colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
                setText(content)
                // Add listener for content changes
                // ...
            }
        },
        update = { editor ->
            if (editor.text.toString() != content) {
                editor.setText(content)
            }
        }
    )
}
