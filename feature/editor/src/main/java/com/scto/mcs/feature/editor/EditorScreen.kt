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
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.LanguageRegistry
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.widget.schemes.SchemeBasic
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.SplitText

@Composable
fun EditorScreen(
    modifier: Modifier = Modifier,
    initialText: String = "// Start coding here!\nfun main() {\n    println(\"Hello, MCS!\")\n}",
    onContentChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val codeEditor = remember {
        CodeEditor(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Basic setup for demonstration
            typefaceText = android.graphics.Typeface.MONOSPACE
            setTextSize(14f)
            setEditorLanguage(TextMateLanguage.create("source.kotlin", true)) // Set Kotlin language
            setColorScheme(TextMateColorScheme.create(context, ThemeRegistry.getInstance().getTheme("darcula")))
            setEdgeEffectEnabled(true)
            setOverScrollEnabled(true)
            setPinLineVerticalList(false)
            setCursorAnimation(true)
            setHighlightCurrentLine(true)
            setScalable(true)
            setScrollBarEnabled(true)

            // Setup syntax highlighting
            FileProviderRegistry.getInstance().addFileProvider(object : FileProviderRegistry.Delegate {
                override fun getContent(path: String): Content {
                    return SplitText("// Placeholder for syntax highlighting files")
                }
            })
            LanguageRegistry.getInstance().loadLanguages(context.assets, "textmate/")
            ThemeRegistry.getInstance().loadThemes(context.assets, "textmate/")


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
            // For now, we are just using initialText and onContentChanged
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
