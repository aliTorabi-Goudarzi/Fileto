package ir.dekot.fileto.feature_compress.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.dekot.fileto.R
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings
import kotlin.math.roundToInt

@Composable
fun CompressionSettingsDialog(
    currentSettings: CompressionSettings,
    onDismiss: () -> Unit,
    onConfirm: (CompressionSettings) -> Unit
) {
    var imageQuality by remember { mutableFloatStateOf(currentSettings.imageQuality.toFloat()) }
    var removeMetadata by remember { mutableStateOf(currentSettings.removeMetadata) }
    var useObjectStreams by remember { mutableStateOf(currentSettings.useObjectStreamCompression) }
    var downscaleDpi by remember { mutableFloatStateOf(currentSettings.downscaleResolution.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.custom_settings)) },
        text = {
            Column {
                // تنظیم کیفیت تصویر
                Text(stringResource(id = R.string.image_quality, imageQuality.roundToInt()))
                Slider(
                    value = imageQuality,
                    onValueChange = { imageQuality = it },
                    valueRange = 0f..100f,
                    steps = 99
                )
                Spacer(modifier = Modifier.height(16.dp))

                // تنظیم رزولوشن تصویر
                Text(stringResource(id = R.string.max_image_resolution, downscaleDpi.roundToInt()))
                Slider(
                    value = downscaleDpi,
                    onValueChange = { downscaleDpi = it },
                    valueRange = 72f..300f,
                    steps = (300-72) - 1
                )
                Spacer(modifier = Modifier.height(16.dp))

                // تنظیم حذف متادیتا
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.remove_metadata), modifier = Modifier.weight(1f))
                    Switch(checked = removeMetadata, onCheckedChange = { removeMetadata = it })
                }
                Spacer(modifier = Modifier.height(8.dp))

                // تنظیم فشرده‌سازی ساختاری
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.structural_compression), modifier = Modifier.weight(1f))
                    Switch(checked = useObjectStreams, onCheckedChange = { useObjectStreams = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newSettings = currentSettings.copy(
                        imageQuality = imageQuality.roundToInt(),
                        removeMetadata = removeMetadata,
                        useObjectStreamCompression = useObjectStreams,
                        downscaleResolution = downscaleDpi.roundToInt()
                    )
                    onConfirm(newSettings)
                }
            ) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}
