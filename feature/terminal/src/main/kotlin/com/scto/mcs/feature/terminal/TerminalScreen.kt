package com.scto.mcs.feature.terminal

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.termux.view.TerminalView // Annahme: TerminalView existiert in :termux:view

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = hiltViewModel()
) {
    AndroidView(
        factory = { context ->
            // Hier wird die TerminalView aus dem :termux:view Modul instanziiert
            TerminalView(context).apply {
                // Initialisierung der View
            }
        },
        update = { view ->
            // Updates der View bei ViewModel-Änderungen
        }
    )
}
