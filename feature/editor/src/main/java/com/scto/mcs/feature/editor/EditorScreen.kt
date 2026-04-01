package com.scto.mcs.feature.editor

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.scto.mcs.core.ui.theme.MobileCodeStudioTheme
import androidx.compose.ui.tooling.preview.Preview
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.LanguageRegistry
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    initialText: String = "// Start coding here!\nfun main() {\n    println(\"Hello, MCS!\")\n}",
    onContentChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val codeEditor = remember {
        // Initialize TextMate grammars and themes if not already done
        // These assets must be placed in app/src/main/assets/textmate/
        // with subdirectories for 'languages' and 'themes', and 'languages.json'/'themes.json' index files.
        if (LanguageRegistry.getInstance().getLanguage("source.kotlin") == null) {
            LanguageRegistry.getInstance().loadLanguages(context.assets, "textmate/")
        }
        if (ThemeRegistry.getInstance().getTheme("darcula") == null) {
            ThemeRegistry.getInstance().loadThemes(context.assets, "textmate/")
        }

        CodeEditor(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Basic setup for demonstration
            typefaceText = android.graphics.Typeface.MONOSPACE
            setTextSize(14f)

            // Set Kotlin language and Darcula theme for syntax highlighting
            setEditorLanguage(TextMateLanguage.create("source.kotlin", true))
            setColorScheme(TextMateColorScheme.create(context, ThemeRegistry.getInstance().getTheme("darcula")))

            // Basic IDE features
            setEdgeEffectEnabled(true)
            setOverScrollEnabled(true)
            setPinLineVerticalList(false) // Pin line numbers to the left
            setCursorAnimation(true)
            setHighlightCurrentLine(true)
            setScalable(true) // Pinch-to-zoom
            setScrollBarEnabled(true)
            setLineNumberPanelWidth(50.dp.value.toInt()) // Adjust line number panel width
            setLineNumberEnabled(true)
            setWordWrap(false)
            setTabWidth(4)

            // Set initial text
            setText(initialText)

            // Listen for content changes
            subscribeEvent(io.github.rosemoe.sora.event.ContentChangeEvent::class.java) { event, _ ->
                onContentChanged(event.newContent.toString())
            }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            codeEditor
        },
        update = { editor ->
            // Update editor properties if needed based on Compose state changes
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEditorScreen() {
    MobileCodeStudioTheme {
        EditorScreen()
    }
}
