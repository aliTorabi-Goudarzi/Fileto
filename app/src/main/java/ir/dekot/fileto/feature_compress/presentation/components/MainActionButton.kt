package ir.dekot.fileto.feature_compress.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.dekot.fileto.R

@Composable
fun MainActionButton(
    isFileSelected: Boolean,
    isCompressing: Boolean,
    onSelectFile: () -> Unit,
    onStartCompression: () -> Unit
) {
    if (isCompressing) {
        CircularProgressIndicator()
    } else {
        Button(
            onClick = if (isFileSelected) onStartCompression else onSelectFile,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = if (isFileSelected) stringResource(id = R.string.start_compression) else stringResource(id = R.string.select_pdf_file))
        }
    }
}