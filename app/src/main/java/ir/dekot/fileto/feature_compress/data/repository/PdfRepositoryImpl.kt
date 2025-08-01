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
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val profileDto = profile.toDto()
            val settingsDto = customSettings?.toDto()
            val destinationUri = localDataSource.compressPdfFile(sourceUri, fileName, profileDto, settingsDto)
            Result.success(destinationUri)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun getFileNameFromUri(uri: Uri): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }

    // --- متد اصلاح شده و کامل برای خواندن حجم فایل ---
    override fun getFileSizeFromUri(uri: Uri): Long? {
        try {
            // روش اصلی و استاندارد
            context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                        return cursor.getLong(sizeIndex)
                    }
                }
            }
            // روش جایگزین برای URI هایی که از روش بالا پشتیبانی نمی‌کنند
            context.contentResolver.openFileDescriptor(uri, "r")?.use {
                return it.statSize
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }
}