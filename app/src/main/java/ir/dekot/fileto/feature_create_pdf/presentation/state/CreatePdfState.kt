package ir.dekot.fileto.feature_create_pdf.presentation.state

import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings
import ir.dekot.fileto.feature_create_pdf.domain.model.ImageItem

data class CreatePdfState(
    // لیست تصاویر انتخاب شده
    val selectedImages: List<ImageItem> = emptyList(),

    // وضعیت‌های مختلف UI
    val isCreatingPdf: Boolean = false,
    val isCompressing: Boolean = false,
    val snackbarMessage: String? = null,
    val showSettingsDialog: Boolean = false,
    val pdfCreationFinished: Boolean = false,

    // تنظیمات فشرده‌سازی
    val isCompressionEnabled: Boolean = false,
    val compressionProfile: CompressionProfile = CompressionProfile.DEFAULT,
    val customSettings: CompressionSettings = CompressionSettings()
)