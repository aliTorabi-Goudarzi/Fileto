package ir.dekot.fileto.feature_history.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val timestamp: Long,
    val compressionProfile: String,
    val customSettingsJson: String?, // برای ذخیره تنظیمات سفارشی به صورت JSON
    val originalSize: Long,
    val compressedSize: Long,
    val compressedFileUri: String,
    val isStarred: Boolean = false // فیلد جدید
)