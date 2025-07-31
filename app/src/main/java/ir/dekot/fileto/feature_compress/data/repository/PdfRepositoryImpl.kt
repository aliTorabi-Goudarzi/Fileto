package ir.dekot.fileto.feature_compress.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext

import ir.dekot.fileto.feature_compress.data.local.datasource.PdfLocalDataSource
import ir.dekot.fileto.feature_compress.data.mapper.toDto
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings
import ir.dekot.fileto.feature_compress.domain.repository.PdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PdfRepositoryImpl @Inject constructor(
    private val localDataSource: PdfLocalDataSource,
    @param:ApplicationContext private val context: Context
) : PdfRepository {
    override suspend fun compressPdf(
        sourceUri: Uri,
        fileName: String,
        profile: CompressionProfile,
        customSettings: CompressionSettings?
    ): Result<Uri> = withContext(Dispatchers.IO) { // خروجی به Result<Uri> تغییر کرد
        try {
            val profileDto = profile.toDto()
            val settingsDto = customSettings?.toDto()
            // حالا DataSource مسئول ساخت فایل و برگرداندن Uri آن است
            val destinationUri = localDataSource.compressPdfFile(sourceUri, fileName, profileDto, settingsDto)
            Result.success(destinationUri)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // پیاده‌سازی متد جدید در لایه Data
    override fun getFileNameFromUri(uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }
}