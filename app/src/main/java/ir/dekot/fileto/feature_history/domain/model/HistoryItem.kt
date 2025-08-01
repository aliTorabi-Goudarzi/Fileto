package ir.dekot.fileto.feature_history.domain.model

data class HistoryItem(
    val id: Int,
    val fileName: String,
    val timestamp: Long,
    val compressionProfile: String,
    val customSettingsJson: String?,
    val originalSize: Long,
    val compressedSize: Long,
    val compressedFileUri: String,
    val isStarred: Boolean // فیلد جدید
)