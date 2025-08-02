package ir.dekot.fileto.feature_compress.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.dekot.fileto.R
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompressionOptions(
    selectedProfile: CompressionProfile,
    onProfileChange: (CompressionProfile) -> Unit,
    onSettingsClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedProfile.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(id = R.string.compression_type)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                CompressionProfile.entries.forEach { profile ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(profile.displayName)
                                if (profile.estimatedReduction.isNotBlank()) {
                                    Badge {
                                        Text(profile.estimatedReduction)
                                    }
                                }
                            }
                        },
                        onClick = {
                            onProfileChange(profile)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // دکمه تنظیمات فقط زمانی فعال است که پروفایل "سفارشی" انتخاب شده باشد
        OutlinedButton(
            onClick = onSettingsClick,
            enabled = selectedProfile == CompressionProfile.CUSTOM,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.advanced_compression_settings))
        }
    }
}