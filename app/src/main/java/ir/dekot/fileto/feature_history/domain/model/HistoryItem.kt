package ir.dekot.fileto.feature_history.domain.model

import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings

data class HistoryItem(
    val id: Int,
    val fileName: String,
    val timestamp: Long,
    val compressionProfile: String,
    // تغییر از String? به آبجکت CompressionSettings?
    val customSettings: CompressionSettings?,
    val originalSize: Long,
    val compressedSize: Long,
    val compressedFileUri: String,
    val isStarred: Boolean
)