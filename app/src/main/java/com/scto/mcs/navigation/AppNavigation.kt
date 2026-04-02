package com.scto.mcs.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.scto.mcs.core.navigation.Screen
import com.scto.mcs.feature.editor.EditorEngine
import com.scto.mcs.feature.editor.EditorScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    editorEngine: EditorEngine
) {
    NavHost(navController = navController, startDestination = Screen.Onboarding.route) {
        composable(Screen.Onboarding.route) {
            Text("Onboarding Placeholder")
        }
        composable(Screen.Setup.route) {
            Text("Setup Placeholder")
        }
        composable(Screen.Dashboard.route) {
            Text("Dashboard Placeholder")
        }
        composable(Screen.Editor.route) {
            EditorScreen(engine = editorEngine)
        }
        composable(Screen.Settings.route) {
            Text("Settings Placeholder")
        }
    }
}
