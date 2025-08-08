package ir.dekot.fileto.feature_create_pdf.presentation.screen
import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ir.dekot.fileto.R
import ir.dekot.fileto.core.utils.CameraUtils
import ir.dekot.fileto.feature_compress.presentation.components.CompressionOptions
import ir.dekot.fileto.feature_compress.presentation.components.CompressionSettingsDialog
import ir.dekot.fileto.feature_create_pdf.domain.model.ImageItem
import ir.dekot.fileto.feature_create_pdf.presentation.state.CreatePdfState
import ir.dekot.fileto.feature_create_pdf.presentation.viewmodel.CreatePdfViewModel
import kotlinx.coroutines.launch
import java.io.File


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreatePdfScreen(
    navController: NavController,
    viewModel: CreatePdfViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris: List<Uri> ->
            viewModel.onImagesSelected(uris.map { it.toString() })
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && capturedImageUri != null) {
                viewModel.onImagesSelected(listOf(capturedImageUri.toString()))
            } else {
                scope.launch { snackbarHostState.showSnackbar("Failed to capture image.") }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                photoFile = CameraUtils.createImageFile(context)
                capturedImageUri = photoFile?.let { CameraUtils.getUriForFile(context, it) }
                capturedImageUri?.let { cameraLauncher.launch(it) }
            } else {
                scope.launch { snackbarHostState.showSnackbar("Camera permission is required.") }
            }
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
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.create_pdf_from_images)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // نمایش لیست تصاویر
            ImagePreviewList(
                images = uiState.selectedImages,
                onMove = viewModel::onMoveImage,
                onRemove = viewModel::onRemoveImage
            )

            // دکمه‌های انتخاب تصویر همیشه نمایش داده می‌شوند
            ImageSelectionButtons(
                onGalleryClick = { galleryLauncher.launch("image/*") },
                onCameraClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // بخش تنظیمات و ساخت PDF (فقط وقتی تصویری انتخاب شده باشد)
            AnimatedVisibility(visible = uiState.selectedImages.isNotEmpty()) {
                CreationSection(
                    uiState = uiState,
                    viewModel = viewModel,
                    onStart = viewModel::startPdfCreation
                )
            }
        }
    }
}

@Composable
private fun ImageSelectionButtons(onGalleryClick: () -> Unit, onCameraClick: () -> Unit) {
    // تغییر اصلی: این بخش دیگر کل صفحه را اشغال نمی‌کند
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(onClick = onGalleryClick, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Gallery")
        }
        OutlinedButton(onClick = onCameraClick, modifier = Modifier.weight(1f)) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Camera")
        }
    }
}

@Composable
private fun CreationSection(uiState: CreatePdfState, viewModel: CreatePdfViewModel, onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
// Compression Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.compress_after_creation), modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.isCompressionEnabled,
                onCheckedChange = viewModel::onToggleCompression
            )
        }

        // Compression Options (shown conditionally)
        if (uiState.isCompressionEnabled) {
            Spacer(modifier = Modifier.height(16.dp))
            CompressionOptions(
                selectedProfile = uiState.compressionProfile,
                onProfileChange = viewModel::onCompressionProfileChanged,
                onSettingsClick = { viewModel.onShowSettingsDialog(true) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Button
        Button(
            onClick = onStart,
            enabled = !uiState.isCreatingPdf && !uiState.isCompressing,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (uiState.isCreatingPdf) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Creating PDF...")
            } else if (uiState.isCompressing) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compressing...")
            } else {
                Text(stringResource(R.string.start_pdf_creation))
            }
        }

        // Settings Dialog
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePreviewList(
    images: List<ImageItem>,
    onMove: (Int, Int) -> Unit,
    onRemove: (ImageItem) -> Unit
) {
    var draggingItem by remember { mutableStateOf<ImageItem?>(null) }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        itemsIndexed(images, key = { _, item -> item.id }) { index, item ->
            Box(
                modifier = Modifier
                    .size(100.dp, 120.dp)
                    .animateItem()
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { },
                            onDragEnd = { },
                            onDrag = { change, _ ->
                                change.consume()
                                val currentX = change.position.x
                                val itemWidth = size.width
                                val newIndex = (index + (currentX / itemWidth).toInt()).coerceIn(0, images.lastIndex)
                                if (newIndex != index) {
                                    onMove(index, newIndex)
                                }
                            }
                        )
                    }
            ) {
                AsyncImage(
                    model = item.uri.toUri(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onRemove(item) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}