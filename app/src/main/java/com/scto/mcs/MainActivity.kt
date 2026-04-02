package com.scto.mcs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.scto.mcs.feature.editor.EditorEngine

class MainActivity : AppCompatActivity() {
    
    private val editorEngine = EditorEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize UI and Editor Engine
        editorEngine.initialize()
    }
}
