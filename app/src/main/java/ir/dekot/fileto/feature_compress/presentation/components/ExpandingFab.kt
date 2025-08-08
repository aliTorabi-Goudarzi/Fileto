package ir.dekot.fileto.feature_compress.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpandingFab(
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCreatePdfClick: () -> Unit // پارامتر جدید
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.End) {
        if (isExpanded) {
            // دکمه جدید برای ساخت PDF
            SmallFloatingActionButton(
                onClick = onCreatePdfClick,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Create PDF from Images")
            }
            SmallFloatingActionButton(
                onClick = onHistoryClick,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.History, contentDescription = "History")
            }
            SmallFloatingActionButton(
                onClick = onSettingsClick,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
        FloatingActionButton(onClick = { isExpanded = !isExpanded }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
    }
}