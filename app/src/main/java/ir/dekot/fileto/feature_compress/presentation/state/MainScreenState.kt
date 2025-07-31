package ir.dekot.fileto.feature_compress.presentation.state

import android.net.Uri
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings

data class MainScreenState(
    val selectedFileUri: Uri? = null,
    val selectedFileName: String = "",
    val isCompressing: Boolean = false,
    val compressionProfile: CompressionProfile = CompressionProfile.DEFAULT,
    val customSettings: CompressionSettings = CompressionSettings(),
    val snackbarMessage: String? = null,
    val showSettingsDialog: Boolean = false
)