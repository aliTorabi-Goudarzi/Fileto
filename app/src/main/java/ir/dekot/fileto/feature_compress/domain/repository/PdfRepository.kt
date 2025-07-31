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
    ): Result<Uri> // خروجی به Uri فایل فشرده شده تغییر کرد

    // متد جدید برای گرفتن نام فایل از URI
    fun getFileNameFromUri(uri: Uri): String?
}