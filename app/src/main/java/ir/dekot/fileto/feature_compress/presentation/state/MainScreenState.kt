package ir.dekot.fileto.feature_compress.presentation.state

import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings

data class MainScreenState(
    val selectedFileUri: String? = null, // از Uri? به String? تغییر کرد
    val selectedFileName: String = "",
    val isCompressing: Boolean = false,
    val compressionProfile: CompressionProfile = CompressionProfile.DEFAULT,
    val customSettings: CompressionSettings = CompressionSettings(),
    val snackbarMessage: String? = null,
    val showSettingsDialog: Boolean = false
)