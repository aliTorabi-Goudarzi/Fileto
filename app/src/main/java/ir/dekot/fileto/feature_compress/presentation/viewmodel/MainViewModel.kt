package ir.dekot.fileto.feature_compress.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// دیگر نیازی به ایمپورت Gson در اینجا نیست
// import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings
import ir.dekot.fileto.feature_compress.domain.usecase.CompressPdfUseCase
import ir.dekot.fileto.feature_compress.domain.usecase.GetFileNameUseCase
import ir.dekot.fileto.feature_compress.domain.usecase.GetFileSizeUseCase
import ir.dekot.fileto.feature_compress.presentation.state.MainScreenState
import ir.dekot.fileto.feature_history.domain.model.HistoryItem
import ir.dekot.fileto.feature_history.domain.usecase.AddHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val compressPdfUseCase: CompressPdfUseCase,
    private val getFileNameUseCase: GetFileNameUseCase,
    private val addHistoryUseCase: AddHistoryUseCase,
    private val getFileSizeUseCase: GetFileSizeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri?) {
        if (uri == null) return
        val uriPath = uri.toString()
        val fileName = getFileNameUseCase(uriPath) ?: "Unknown File"
        _uiState.update {
            it.copy(
                selectedFileUri = uriPath,
                selectedFileName = fileName
            )
        }
    }

    // ... (متدهای دیگر بدون تغییر باقی می‌مانند) ...
    fun onResetFileSelection() {
        _uiState.update { MainScreenState() }
    }

    fun onCompressionProfileChanged(profile: CompressionProfile) {
        _uiState.update { it.copy(compressionProfile = profile) }
    }


    fun onStartCompression() {
        val sourceUriPath = _uiState.value.selectedFileUri ?: return
        val fileName = _uiState.value.selectedFileName
        val originalSize = getFileSizeUseCase(sourceUriPath) ?: 0L

        viewModelScope.launch {
            _uiState.update { it.copy(isCompressing = true, snackbarMessage = null) }

            val result = compressPdfUseCase(
                sourceUriPath = sourceUriPath,
                fileName = fileName,
                profile = _uiState.value.compressionProfile,
                customSettings = if (_uiState.value.compressionProfile == CompressionProfile.CUSTOM) {
                    _uiState.value.customSettings
                } else {
                    null
                }
            )

            result.onSuccess { compressedFileUriPath ->
                val compressedSize = getFileSizeUseCase(compressedFileUriPath) ?: 0L
                val historyItem = HistoryItem(
                    id = 0,
                    fileName = fileName,
                    timestamp = System.currentTimeMillis(),
                    compressionProfile = _uiState.value.compressionProfile.displayName,
                    // <<-- تغییر اصلی اینجاست -->>
                    // دیگر JSON را نمی‌سازیم و خود آبجکت را پاس می‌دهیم
                    customSettings = if (_uiState.value.compressionProfile == CompressionProfile.CUSTOM) {
                        _uiState.value.customSettings
                    } else null,
                    originalSize = originalSize,
                    compressedSize = compressedSize,
                    compressedFileUri = compressedFileUriPath,
                    isStarred = false
                )
                addHistoryUseCase(historyItem)

                _uiState.update {
                    MainScreenState(snackbarMessage = "فایل با موفقیت فشرده و ذخیره شد!")
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isCompressing = false,
                        snackbarMessage = "خطا: ${exception.message}"
                    )
                }
            }
        }
    }

    // ... (بقیه متدها بدون تغییر باقی می‌مانند) ...
    fun onShowSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun onCustomSettingsChanged(settings: CompressionSettings) {
        _uiState.update { it.copy(customSettings = settings) }
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}