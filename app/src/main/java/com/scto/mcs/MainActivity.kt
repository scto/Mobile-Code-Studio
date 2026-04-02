package com.scto.mcs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scto.mcs.core.navigation.Screen
import com.scto.mcs.feature.editor.EditorEngine
import com.scto.mcs.feature.editor.EditorScreen

class MainActivity : ComponentActivity() {
    
    private val editorEngine = EditorEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        editorEngine.initialize()

        setContent {
            val navController = rememberNavController()
            
            NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
                composable(Screen.Dashboard.route) {
                    Text("Dashboard Placeholder")
                }
                composable(Screen.Editor.route) {
                    EditorScreen(engine = editorEngine)
                }
                composable(Screen.Onboarding.route) {
                    Text("Onboarding Placeholder")
                }
                composable(Screen.Settings.route) {
                    Text("Settings Placeholder")
                }
            }
        }
    }
}
