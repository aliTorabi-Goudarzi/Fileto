package ir.dekot.fileto.feature_compress.domain.usecase

import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings
import ir.dekot.fileto.feature_compress.domain.repository.PdfRepository
import javax.inject.Inject

class CompressPdfUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    suspend operator fun invoke(
        sourceUriPath: String, // از Uri به String تغییر کرد
        fileName: String,
        profile: CompressionProfile,
        customSettings: CompressionSettings? = null
    ): Result<String> { // خروجی به Result<String> تغییر کرد
        if (fileName.isBlank()) {
            return Result.failure(IllegalArgumentException("File name cannot be empty."))
        }
        return repository.compressPdf(sourceUriPath, fileName, profile, customSettings)
    }
}