package com.scto.mcs.core.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Editor : Screen("editor")
    object Settings : Screen("settings")
}
