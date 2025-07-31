package ir.dekot.fileto.feature_compress.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.onFileSelected(uri)
        }
    )

    LaunchedEffect(uiState.snackbarMessage) {
        if (uiState.snackbarMessage != null) {
            scope.launch {
                snackbarHostState.showSnackbar(uiState.snackbarMessage!!)
                viewModel.onSnackbarShown()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FileSelectionHeader(
                fileName = uiState.selectedFileName,
                onReset = viewModel::onResetFileSelection
            )

            Spacer(modifier = Modifier.height(32.dp))

            MainActionButton(
                isFileSelected = uiState.selectedFileUri != null,
                isCompressing = uiState.isCompressing,
                onSelectFile = { filePickerLauncher.launch("application/pdf") },
                onStartCompression = viewModel::onStartCompression
            )

            if (uiState.selectedFileUri != null) {
                Spacer(modifier = Modifier.height(24.dp))
                CompressionOptions(
                    selectedProfile = uiState.compressionProfile,
                    onProfileChange = viewModel::onCompressionProfileChanged,
                    onSettingsClick = { viewModel.onShowSettingsDialog(true) }
                )
            }

            if (uiState.showSettingsDialog) {
                CompressionSettingsDialog(
                    onDismiss = { viewModel.onShowSettingsDialog(false) }
                )
            }
        }
    }
}

// کامپوننت‌های دیگر (FileSelectionHeader, MainActionButton, و ...) بدون تغییر باقی می‌مانند.
@Composable
fun FileSelectionHeader(fileName: String, onReset: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fileName.ifEmpty { "فایلی انتخاب نشده است" },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            if (fileName.isNotEmpty()) {
                IconButton(onClick = onReset) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "انتخاب مجدد فایل"
                    )
                }
            }
        }
    }
}

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
            Text(text = if (isFileSelected) "شروع فشرده‌سازی" else "انتخاب فایل PDF")
        }
    }
}

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
                label = { Text("نوع فشرده‌سازی") },
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
                    if (profile != CompressionProfile.CUSTOM) { // فعلا گزینه سفارشی را نمایش نمی‌دهیم
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("تنظیمات پیشرفته فشرده‌سازی")
        }
    }
}

@Composable
fun CompressionSettingsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تنظیمات پیشرفته") },
        text = {
            Text("این بخش در آینده پیاده‌سازی خواهد شد.")
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("تایید")
            }
        }
    )
}