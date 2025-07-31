package ir.dekot.fileto.feature_compress.presentation.state

import android.net.Uri
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile

data class MainScreenState(
    val selectedFileUri: Uri? = null,
    val selectedFileName: String = "",
    val isCompressing: Boolean = false,
    val compressionProfile: CompressionProfile = CompressionProfile.DEFAULT,
    val snackbarMessage: String? = null, // برای نمایش پیام‌های موفقیت و خطا
    val showSettingsDialog: Boolean = false
)