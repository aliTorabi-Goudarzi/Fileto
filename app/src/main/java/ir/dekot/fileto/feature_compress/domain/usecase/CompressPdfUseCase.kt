package ir.dekot.fileto.feature_compress.domain.usecase

import android.net.Uri
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings
import ir.dekot.fileto.feature_compress.domain.repository.PdfRepository
import javax.inject.Inject

class CompressPdfUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    suspend operator fun invoke(
        sourceUri: Uri,
        fileName: String,
        profile: CompressionProfile,
        customSettings: CompressionSettings? = null
    ): Result<Uri> { // خروجی به Uri تغییر کرد
        if (fileName.isBlank()) {
            return Result.failure(IllegalArgumentException("File name cannot be empty."))
        }
        return repository.compressPdf(sourceUri, fileName, profile, customSettings)
    }
}