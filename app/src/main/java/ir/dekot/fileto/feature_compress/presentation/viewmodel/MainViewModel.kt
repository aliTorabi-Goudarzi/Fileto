package ir.dekot.fileto.feature_compress.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.usecase.CompressPdfUseCase
import ir.dekot.fileto.feature_compress.domain.usecase.GetFileNameUseCase
import ir.dekot.fileto.feature_compress.presentation.state.MainScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val compressPdfUseCase: CompressPdfUseCase,
    private val getFileNameUseCase: GetFileNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri?) {
        if (uri == null) return
        val fileName = getFileNameUseCase((uri)) ?: "Unknown File"
        _uiState.update {
            it.copy(
                selectedFileUri = uri,
                selectedFileName = fileName
            )
        }
    }

    fun onResetFileSelection() {
        _uiState.update { MainScreenState() } // ریست کامل State
    }

    fun onCompressionProfileChanged(profile: CompressionProfile) {
        _uiState.update { it.copy(compressionProfile = profile) }
    }

    fun onStartCompression() {
        val sourceUri = _uiState.value.selectedFileUri ?: return
        val fileName = _uiState.value.selectedFileName

        viewModelScope.launch {
            _uiState.update { it.copy(isCompressing = true, snackbarMessage = null) }

            val result = compressPdfUseCase(
                sourceUri = sourceUri,
                fileName = fileName,
                profile = _uiState.value.compressionProfile
            )

            result.onSuccess { compressedFileUri ->
                // پس از موفقیت، State را ریست می‌کنیم و پیام موفقیت را تنظیم می‌کنیم
                _uiState.update {
                    MainScreenState(snackbarMessage = "فایل با موفقیت فشرده و ذخیره شد!")
                }
                // TODO: اطلاعات فایل فشرده شده را در دیتابیس تاریخچه ذخیره کن
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

    fun onShowSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}