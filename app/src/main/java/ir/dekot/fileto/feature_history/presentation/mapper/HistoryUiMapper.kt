package ir.dekot.fileto.feature_history.presentation.mapper

import ir.dekot.fileto.feature_history.domain.model.HistoryItem
import ir.dekot.fileto.feature_history.presentation.screen.HistoryUiItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.pow

class HistoryUiMapper @Inject constructor() {

    fun toUiItem(domainItem: HistoryItem): HistoryUiItem {
        val reduction = if (domainItem.originalSize > 0) {
            100 - (domainItem.compressedSize * 100 / domainItem.originalSize)
        } else 0

        val customSettingsList: List<Pair<String, String>>? = domainItem.customSettings?.let { settings ->
            buildList {
                add("کیفیت تصویر" to "${settings.imageQuality}%")
                add("حذف متادیتا" to if (settings.removeMetadata) "بله" else "خیر")
                add("فشرده‌سازی ساختاری" to if (settings.useObjectStreamCompression) "بله" else "خیر")
                add("حداکثر رزولوشن" to "${settings.downscaleResolution} DPI")
            }
        }

        return HistoryUiItem(
            id = domainItem.id,
            fileName = domainItem.fileName,
            formattedDate = formatDate(domainItem.timestamp),
            compressionProfile = domainItem.compressionProfile,
            customSettings = customSettingsList,
            formattedSize = "${formatFileSize(domainItem.originalSize)} -> ${formatFileSize(domainItem.compressedSize)}",
            reductionPercentage = reduction.toInt(),
            compressedFileUri = domainItem.compressedFileUri,
            isStarred = domainItem.isStarred
        )
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(timestamp))
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return String.format(Locale.US, "%.2f %s", bytes / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
    }
}