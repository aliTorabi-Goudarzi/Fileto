package ir.dekot.fileto.feature_history.data.mapper

import com.google.gson.Gson
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings
import ir.dekot.fileto.feature_history.data.local.entity.HistoryEntity
import ir.dekot.fileto.feature_history.domain.model.HistoryItem

// یک نمونه از Gson برای استفاده در تبدیل‌ها
private val gson = Gson()

fun HistoryEntity.toDomain(): HistoryItem {
    return HistoryItem(
        id = id,
        fileName = fileName,
        timestamp = timestamp,
        compressionProfile = compressionProfile,
        // تبدیل رشته JSON به آبجکت در زمان خواندن از دیتا
        customSettings = customSettingsJson?.let {
            try {
                gson.fromJson(it, CompressionSettings::class.java)
            } catch (_: Exception) {
                null // در صورت خطا، null برمی‌گردانیم
            }
        },
        originalSize = originalSize,
        compressedSize = compressedSize,
        compressedFileUri = compressedFileUri,
        isStarred = isStarred
    )
}

fun HistoryItem.toEntity(): HistoryEntity {
    return HistoryEntity(
        id = id,
        fileName = fileName,
        timestamp = timestamp,
        compressionProfile = compressionProfile,
        // تبدیل آبجکت به رشته JSON در زمان نوشتن در دیتا
        customSettingsJson = customSettings?.let { gson.toJson(it) },
        originalSize = originalSize,
        compressedSize = compressedSize,
        compressedFileUri = compressedFileUri,
        isStarred = isStarred
    )
}