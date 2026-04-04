package com.scto.mcs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.scto.mcs.feature.editor.EditorEngine
import com.scto.mcs.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject lateinit var editorEngine: EditorEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        editorEngine.initialize()

        setContent {
            val navController = rememberNavController()
            AppNavigation(navController = navController, editorEngine = editorEngine)
        }
    }
}
