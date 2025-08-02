package ir.dekot.fileto.feature_compress.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.dekot.fileto.core.navigation.Screen
import ir.dekot.fileto.feature_compress.presentation.components.CompressionOptions
import ir.dekot.fileto.feature_compress.presentation.components.CompressionSettingsDialog
import ir.dekot.fileto.feature_compress.presentation.components.ExpandingFab
import ir.dekot.fileto.feature_compress.presentation.components.FileSelectionHeader
import ir.dekot.fileto.feature_compress.presentation.components.MainActionButton
import ir.dekot.fileto.feature_compress.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navController: NavController, // NavController اضافه شد
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                floatingActionButton = {
            ExpandingFab(
                onHistoryClick = { navController.navigate(Screen.HistoryScreen.route) },
                onSettingsClick = { navController.navigate(Screen.SettingsScreen.route) }
            )
        }
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
                    currentSettings = uiState.customSettings,
                    onDismiss = { viewModel.onShowSettingsDialog(false) },
                    onConfirm = { newSettings ->
                        viewModel.onCustomSettingsChanged(newSettings)
                        viewModel.onShowSettingsDialog(false)
                    }
                )
            }
        }
    }
}