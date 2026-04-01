package com.scto.mcs.feature.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scto.mcs.domain.dashboard.model.ProjectTemplate
import com.scto.mcs.core.ui.theme.MobileCodeStudioTheme // Assuming core:ui exports the theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    onTemplateSelected: (ProjectTemplate) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Project") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back or close */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Back") // Placeholder icon
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Choose a project template",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val templates = listOf(
                ProjectTemplate(
                    id = "empty_activity",
                    name = "Empty Activity",
                    description = "Creates a new empty activity."
                ),
                ProjectTemplate(
                    id = "basic_activity",
                    name = "Basic Activity",
                    description = "Creates a new basic activity with a menu and sample content."
                ),
                ProjectTemplate(
                    id = "bottom_navigation_activity",
                    name = "Bottom Navigation Activity",
                    description = "Creates an activity with a bottom navigation bar."
                ),
                ProjectTemplate(
                    id = "tabbed_activity",
                    name = "Tabbed Activity",
                    description = "Creates an activity with a tab layout."
                )
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(templates) { template ->
                    TemplateCard(template = template) {
                        onTemplateSelected(template)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateCard(template: ProjectTemplate, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = template.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = template.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateProjectScreen() {
    MobileCodeStudioTheme {
        CreateProjectScreen()
    }
}
