package ir.dekot.fileto.feature_compress.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dekot.fileto.feature_compress.data.local.datasource.PdfLocalDataSource
import ir.dekot.fileto.feature_compress.data.mapper.toDto
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings
import ir.dekot.fileto.feature_compress.domain.repository.PdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.net.toUri

class PdfRepositoryImpl @Inject constructor(
    private val localDataSource: PdfLocalDataSource,
    @param:ApplicationContext private val context: Context // این وابستگی دیگر برای متدهای نام و حجم نیاز نیست، اما ممکن است برای کارهای آینده نگه داشته شود
) : PdfRepository {

    override suspend fun compressPdf(
        sourceUriPath: String,
        fileName: String,
        profile: CompressionProfile,
        customSettings: CompressionSettings?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val sourceUri = sourceUriPath.toUri() // تبدیل به Uri قبل از ارسال به DataSource
            val profileDto = profile.toDto()
            val settingsDto = customSettings?.toDto()

            val destinationUri = localDataSource.compressPdfFile(sourceUri, fileName, profileDto, settingsDto)
            Result.success(destinationUri.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * پیاده‌سازی این متد اکنون فقط به DataSource واگذار می‌شود
     */
    override fun getFileNameFromUri(uriPath: String): String? {
        // دیگر منطقی اینجا وجود ندارد، فقط فراخوانی
        return localDataSource.getFileNameFromUri(uriPath)
    }

    /**
     * پیاده‌سازی این متد اکنون فقط به DataSource واگذار می‌شود
     */
    override fun getFileSizeFromUri(uriPath: String): Long? {
        // دیگر منطقی اینجا وجود ندارد، فقط فراخوانی
        return localDataSource.getFileSizeFromUri(uriPath)
    }
}