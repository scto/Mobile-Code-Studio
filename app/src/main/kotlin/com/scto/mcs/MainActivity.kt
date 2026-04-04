package com.scto.mcs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.scto.mcs.feature.editor.EditorEngine
import com.scto.mcs.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    
    private val editorEngine = EditorEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        editorEngine.initialize()

        setContent {
            val navController = rememberNavController()
            AppNavigation(navController = navController, editorEngine = editorEngine)
        }
    }
}
