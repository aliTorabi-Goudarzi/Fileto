package ir.dekot.fileto.feature_compress.domain.repository

import android.net.Uri
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings

interface PdfRepository {
    suspend fun compressPdf(
        sourceUri: Uri,
        fileName: String,
        profile: CompressionProfile,
        customSettings: CompressionSettings? = null
    ): Result<Uri>

    fun getFileNameFromUri(uri: Uri): String?
    fun getFileSizeFromUri(uri: Uri): Long?
}