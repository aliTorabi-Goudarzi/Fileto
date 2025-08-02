package ir.dekot.fileto.feature_compress.domain.repository

import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings

interface PdfRepository {
    /**
     * @param sourceUriPath مسیر فایل منبع به صورت رشته (String)
     * @return Result<String> مسیر فایل فشرده شده به صورت رشته (String)
     */
    suspend fun compressPdf(
        sourceUriPath: String,
        fileName: String,
        profile: CompressionProfile,
        customSettings: CompressionSettings? = null
    ): Result<String>

    /**
     * @param uriPath مسیر فایل به صورت رشته (String)
     */
    fun getFileNameFromUri(uriPath: String): String?

    /**
     * @param uriPath مسیر فایل به صورت رشته (String)
     */
    fun getFileSizeFromUri(uriPath: String): Long?
    }