package ir.dekot.fileto.feature_pdf_tools.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ir.dekot.fileto.R
import ir.dekot.fileto.feature_pdf_tools.presentation.components.FileSelectionCard
import ir.dekot.fileto.feature_pdf_tools.presentation.components.SplitOptionsCard
import ir.dekot.fileto.feature_pdf_tools.presentation.components.SplitProgressCard
import ir.dekot.fileto.feature_pdf_tools.presentation.state.SplitPdfEvent
import ir.dekot.fileto.feature_pdf_tools.presentation.viewmodel.SplitPdfViewModel

/**
 * صفحه تقسیم PDF
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitPdfScreen(
    navController: NavController,
    viewModel: SplitPdfViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // لانچر برای انتخاب فایل PDF
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(SplitPdfEvent.FileSelected(it.toString()))
        }
    }
    
    // نمایش پیام‌های خطا
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(SplitPdfEvent.ClearError)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(stringResource(id = R.string.split_pdf)) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // کارت انتخاب فایل
            FileSelectionCard(
                selectedFileName = uiState.selectedFileName,
                pdfInfo = uiState.pdfInfo,
                isLoadingPdfInfo = uiState.isLoadingPdfInfo,
                onSelectFile = {
                    pdfPickerLauncher.launch("application/pdf")
                }
            )
            
            // کارت تنظیمات تقسیم
            if (uiState.selectedFileUri != null) {
                SplitOptionsCard(
                    selectedSplitType = uiState.splitOptions.splitType,
                    pageRangeInput = uiState.pageRangeInput,
                    pagesPerSplitInput = uiState.pagesPerSplitInput,
                    onSplitTypeChanged = { splitType ->
                        viewModel.onEvent(SplitPdfEvent.SplitTypeChanged(splitType))
                    },
                    onPageRangeChanged = { range ->
                        viewModel.onEvent(SplitPdfEvent.PageRangeChanged(range))
                    },
                    onPagesPerSplitChanged = { pages ->
                        viewModel.onEvent(SplitPdfEvent.PagesPerSplitChanged(pages))
                    }
                )
            }
            
            // کارت پیشرفت تقسیم
            SplitProgressCard(
                isSplitting = uiState.isSplitting,
                progress = uiState.splitProgress,
                successMessage = uiState.successMessage,
                errorMessage = null, // خطاها از طریق Snackbar نمایش داده می‌شوند
                outputFiles = uiState.outputFiles
            )
            
            // دکمه شروع تقسیم
            if (uiState.selectedFileUri != null && !uiState.isSplitting) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        viewModel.onEvent(SplitPdfEvent.StartSplit)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoadingPdfInfo
                ) {
                    Text(
                        text = stringResource(id = R.string.start_split),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
