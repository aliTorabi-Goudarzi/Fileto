package ir.dekot.fileto.feature_pdf_tools.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitOptions
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitType
import ir.dekot.fileto.feature_pdf_tools.domain.usecase.GetPdfInfoUseCase
import ir.dekot.fileto.feature_pdf_tools.domain.usecase.SplitPdfUseCase
import ir.dekot.fileto.feature_pdf_tools.presentation.state.SplitPdfEvent
import ir.dekot.fileto.feature_pdf_tools.presentation.state.SplitPdfState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel برای مدیریت صفحه تقسیم PDF
 */
@HiltViewModel
class SplitPdfViewModel @Inject constructor(
    private val splitPdfUseCase: SplitPdfUseCase,
    private val getPdfInfoUseCase: GetPdfInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplitPdfState())
    val uiState: StateFlow<SplitPdfState> = _uiState.asStateFlow()

    /**
     * مدیریت رویدادهای UI
     */
    fun onEvent(event: SplitPdfEvent): Unit {
        when (event) {
            is SplitPdfEvent.FileSelected -> handleFileSelected(event.uri)
            is SplitPdfEvent.SplitTypeChanged -> handleSplitTypeChanged(event.splitType)
            is SplitPdfEvent.PageRangeChanged -> handlePageRangeChanged(event.range)
            is SplitPdfEvent.PagesPerSplitChanged -> handlePagesPerSplitChanged(event.pages)
            SplitPdfEvent.StartSplit -> handleStartSplit()
            SplitPdfEvent.ClearError -> clearError()
            SplitPdfEvent.ClearSuccess -> clearSuccess()
            SplitPdfEvent.ResetState -> resetState()
        }
    }

    /**
     * مدیریت انتخاب فایل
     */
    private fun handleFileSelected(uri: String): Unit {
        _uiState.update { 
            it.copy(
                selectedFileUri = uri,
                isLoadingPdfInfo = true,
                errorMessage = null
            ) 
        }

        viewModelScope.launch {
            getPdfInfoUseCase(uri).fold(
                onSuccess = { pdfInfo ->
                    _uiState.update { 
                        it.copy(
                            pdfInfo = pdfInfo,
                            selectedFileName = pdfInfo.fileName,
                            isLoadingPdfInfo = false
                        ) 
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isLoadingPdfInfo = false,
                            errorMessage = "خطا در بارگذاری اطلاعات فایل: ${error.message}"
                        ) 
                    }
                }
            )
        }
    }

    /**
     * مدیریت تغییر نوع تقسیم
     */
    private fun handleSplitTypeChanged(splitType: SplitType): Unit {
        _uiState.update { 
            it.copy(
                splitOptions = it.splitOptions.copy(splitType = splitType),
                errorMessage = null
            ) 
        }
    }

    /**
     * مدیریت تغییر بازه صفحات
     */
    private fun handlePageRangeChanged(range: String): Unit {
        _uiState.update { 
            it.copy(
                pageRangeInput = range,
                errorMessage = null
            ) 
        }
    }

    /**
     * مدیریت تغییر تعداد صفحات در هر قسمت
     */
    private fun handlePagesPerSplitChanged(pages: String): Unit {
        _uiState.update { 
            it.copy(
                pagesPerSplitInput = pages,
                errorMessage = null
            ) 
        }
    }

    /**
     * شروع عملیات تقسیم
     */
    private fun handleStartSplit(): Unit {
        val currentState = _uiState.value
        
        if (currentState.selectedFileUri == null) {
            _uiState.update { it.copy(errorMessage = "لطفاً ابتدا فایل PDF را انتخاب کنید") }
            return
        }

        val splitOptions = buildSplitOptions(currentState) ?: return

        _uiState.update { 
            it.copy(
                isSplitting = true,
                splitProgress = 0f,
                errorMessage = null
            ) 
        }

        viewModelScope.launch {
            // فعلاً از مسیر ثابت برای خروجی استفاده می‌کنیم
            // بعداً می‌توان از Storage Access Framework استفاده کرد
            val outputDirectory = "/storage/emulated/0/Download/FileTo_Split"
            
            splitPdfUseCase(
                inputFilePath = currentState.selectedFileUri,
                splitOptions = splitOptions,
                outputDirectory = outputDirectory
            ).fold(
                onSuccess = { result ->
                    _uiState.update { 
                        it.copy(
                            isSplitting = false,
                            splitProgress = 1f,
                            outputFiles = result.outputFiles,
                            successMessage = "فایل با موفقیت به ${result.splitCount} قسمت تقسیم شد"
                        ) 
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isSplitting = false,
                            splitProgress = 0f,
                            errorMessage = "خطا در تقسیم فایل: ${error.message}"
                        ) 
                    }
                }
            )
        }
    }

    /**
     * ساخت تنظیمات تقسیم بر اساس ورودی کاربر
     */
    private fun buildSplitOptions(state: SplitPdfState): SplitOptions? {
        return when (state.splitOptions.splitType) {
            SplitType.EACH_PAGE -> {
                SplitOptions(splitType = SplitType.EACH_PAGE)
            }
            SplitType.BY_PAGES -> {
                val pagesPerSplit = state.pagesPerSplitInput.toIntOrNull()
                if (pagesPerSplit == null || pagesPerSplit <= 0) {
                    _uiState.update { it.copy(errorMessage = "تعداد صفحات باید عددی مثبت باشد") }
                    return null
                }
                SplitOptions(
                    splitType = SplitType.BY_PAGES,
                    pagesPerSplit = pagesPerSplit
                )
            }
            SplitType.BY_RANGE -> {
                if (state.pageRangeInput.isBlank()) {
                    _uiState.update { it.copy(errorMessage = "لطفاً بازه صفحات را وارد کنید") }
                    return null
                }
                val ranges = state.pageRangeInput.split(",").map { it.trim() }
                SplitOptions(
                    splitType = SplitType.BY_RANGE,
                    pageRanges = ranges
                )
            }
        }
    }

    private fun clearError(): Unit {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun clearSuccess(): Unit {
        _uiState.update { it.copy(successMessage = null) }
    }

    private fun resetState(): Unit {
        _uiState.value = SplitPdfState()
    }
}
