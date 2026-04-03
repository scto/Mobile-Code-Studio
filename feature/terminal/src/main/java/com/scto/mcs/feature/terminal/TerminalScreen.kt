package com.scto.mcs.feature.terminal

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.termux.view.TerminalView

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.startSession()
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            TerminalView(context)
        },
        update = { view ->
            // Update view properties if needed
        }
    )
}
