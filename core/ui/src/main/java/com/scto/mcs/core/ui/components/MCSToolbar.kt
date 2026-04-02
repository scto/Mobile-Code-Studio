package com.scto.mcs.core.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCSToolbar(title: String, actions: @Composable () -> Unit = {}) {
    TopAppBar(
        title = { Text(title) },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
