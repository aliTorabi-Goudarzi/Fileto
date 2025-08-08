package ir.dekot.fileto.feature_create_pdf.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings
import ir.dekot.fileto.feature_compress.domain.usecase.CompressPdfUseCase
import ir.dekot.fileto.feature_compress.domain.usecase.GetFileNameUseCase
import ir.dekot.fileto.feature_compress.domain.usecase.GetFileSizeUseCase
import ir.dekot.fileto.feature_create_pdf.domain.model.ImageItem
import ir.dekot.fileto.feature_create_pdf.domain.usecase.CreatePdfFromImagesUseCase
import ir.dekot.fileto.feature_create_pdf.presentation.state.CreatePdfState
import ir.dekot.fileto.feature_history.domain.model.HistoryItem
import ir.dekot.fileto.feature_history.domain.usecase.AddHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePdfViewModel @Inject constructor(
    private val createPdfFromImagesUseCase: CreatePdfFromImagesUseCase,
    private val compressPdfUseCase: CompressPdfUseCase,
    private val getFileSizeUseCase: GetFileSizeUseCase,
    private val getFileNameUseCase: GetFileNameUseCase,
    private val addHistoryUseCase: AddHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePdfState())
    val uiState = _uiState.asStateFlow()

    fun onImagesSelected(uris: List<String>) {
        val newImages = uris.map { ImageItem(id = it, uri = it) }
        _uiState.update {
            it.copy(selectedImages = it.selectedImages + newImages)
        }
    }

    fun onRemoveImage(image: ImageItem) {
        _uiState.update {
            it.copy(selectedImages = it.selectedImages - image)
        }
    }

    fun onMoveImage(from: Int, to: Int) {
        _uiState.update {
            val mutableList = it.selectedImages.toMutableList()
            val movedItem = mutableList.removeAt(from)
            mutableList.add(to, movedItem)
            it.copy(selectedImages = mutableList)
        }
    }

    fun onToggleCompression(isEnabled: Boolean) {
        _uiState.update { it.copy(isCompressionEnabled = isEnabled) }
    }

    fun onCompressionProfileChanged(profile: CompressionProfile) {
        _uiState.update { it.copy(compressionProfile = profile) }
    }

    fun onCustomSettingsChanged(settings: CompressionSettings) {
        _uiState.update { it.copy(customSettings = settings) }
    }

    fun onShowSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun startPdfCreation() {
        if (_uiState.value.selectedImages.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingPdf = true, snackbarMessage = null) }
            val fileName = "Created_PDF_${System.currentTimeMillis()}"

            val creationResult = createPdfFromImagesUseCase(
                images = _uiState.value.selectedImages,
                fileName = fileName
            )

            creationResult.onSuccess { createdPdfUri ->
                _uiState.update { it.copy(isCreatingPdf = false, pdfCreationFinished = true) }

                if (_uiState.value.isCompressionEnabled) {
                    _uiState.update { it.copy(isCompressing = true, snackbarMessage = "PDF با موفقیت ساخته شد، در حال فشرده‌سازی...") }
                    compressCreatedPdf(createdPdfUri)
                } else {
                    _uiState.update { it.copy(snackbarMessage = "PDF با موفقیت ساخته و ذخیره شد!") }
                    addHistory(
                        originalUri = createdPdfUri,
                        compressedUri = null
                    )
                    resetState()
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isCreatingPdf = false,
                        snackbarMessage = "خطا در ساخت PDF: ${exception.message}"
                    )
                }
            }
        }
    }

    private suspend fun compressCreatedPdf(pdfUri: String) {
        val fileName = getFileNameUseCase(pdfUri) ?: "Unknown File"
        val compressionResult = compressPdfUseCase(
            sourceUriPath = pdfUri,
            fileName = fileName,
            profile = _uiState.value.compressionProfile,
            customSettings = if (_uiState.value.compressionProfile == CompressionProfile.CUSTOM) {
                _uiState.value.customSettings
            } else null
        )

        compressionResult.onSuccess { compressedPdfUri ->
            _uiState.update { it.copy(isCompressing = false, snackbarMessage = "فشرده‌سازی با موفقیت انجام شد!") }
            addHistory(
                originalUri = pdfUri,
                compressedUri = compressedPdfUri
            )
        }.onFailure { exception ->
            _uiState.update {
                it.copy(
                    isCompressing = false,
                    snackbarMessage = "خطا در فشرده‌سازی: ${exception.message}"
                )
            }
        }
        resetState()
    }

    private suspend fun addHistory(originalUri: String, compressedUri: String?) {
        val originalSize = getFileSizeUseCase(originalUri) ?: 0L
        val compressedSize = compressedUri?.let { getFileSizeUseCase(it) } ?: originalSize
        val finalUri = compressedUri ?: originalUri
        val fileName = getFileNameUseCase(finalUri) ?: "Created PDF"

        val historyItem = HistoryItem(
            id = 0,
            fileName = fileName,
            timestamp = System.currentTimeMillis(),
            // یک پروفایل جدید برای تاریخچه تعریف می‌کنیم
            compressionProfile = if(compressedUri != null) _uiState.value.compressionProfile.displayName else "فقط ساخت",
            customSettings = if (compressedUri != null && _uiState.value.compressionProfile == CompressionProfile.CUSTOM) {
                _uiState.value.customSettings
            } else null,
            originalSize = originalSize,
            compressedSize = compressedSize,
            compressedFileUri = finalUri,
            isStarred = false
        )
        addHistoryUseCase(historyItem)
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun resetState() {
        // بعد از چند ثانیه، State را ریست می‌کنیم تا برای ساخت PDF بعدی آماده باشد
        kotlinx.coroutines.GlobalScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.update { CreatePdfState() }
        }
    }
}