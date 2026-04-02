package com.scto.mcs.core.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.scto.mcs.core.ui.icons.MCSIcons

@Composable
fun FileIcon(extension: String) {
    val icon = if (extension == "kt") MCSIcons.File else MCSIcons.Folder
    Icon(imageVector = icon, contentDescription = null)
}
