package ir.dekot.fileto.feature_history.presentation.screen

data class HistoryUiItem(
    val id: Int,
    val fileName: String,
    val formattedDate: String,
    val compressionProfileName: String, // نام enum (مثلا "DEFAULT") ذخیره می‌شود
    val customSettings: List<Pair<String, String>>?, // جایگزین رشته JSON
    val formattedSize: String,
    val reductionPercentage: Int,
    val compressedFileUri: String,
    val isStarred: Boolean // فیلد جدید
)