package ir.dekot.fileto.feature_history.data.mapper

import ir.dekot.fileto.feature_history.data.local.entity.HistoryEntity
import ir.dekot.fileto.feature_history.domain.model.HistoryItem

fun HistoryEntity.toDomain(): HistoryItem {
    return HistoryItem(
        id = id,
        fileName = fileName,
        timestamp = timestamp,
        compressionProfile = compressionProfile,
        customSettingsJson = customSettingsJson,
        originalSize = originalSize,
        compressedSize = compressedSize,
        compressedFileUri = compressedFileUri,
        isStarred = isStarred // مپ کردن فیلد جدید
    )
}

fun HistoryItem.toEntity(): HistoryEntity {
    return HistoryEntity(
        id = id,
        fileName = fileName,
        timestamp = timestamp,
        compressionProfile = compressionProfile,
        customSettingsJson = customSettingsJson,
        originalSize = originalSize,
        compressedSize = compressedSize,
        compressedFileUri = compressedFileUri,
        isStarred = isStarred // مپ کردن فیلد جدید
    )
}