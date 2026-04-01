package com.scto.mcs.feature.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TemplateSelectionScreen(onTemplateSelected: (String) -> Unit) {
    val templates = listOf("Empty Compose Activity", "Android Library")
    
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.padding(16.dp)) {
        items(templates.size) { index ->
            Card(modifier = Modifier.padding(8.dp).clickable { onTemplateSelected(templates[index]) }) {
                Text(templates[index], modifier = Modifier.padding(16.dp))
            }
        }
    }
}
