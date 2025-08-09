package ir.dekot.fileto.feature_pdf_tools.domain.usecase

import ir.dekot.fileto.feature_pdf_tools.domain.model.PdfInfo
import ir.dekot.fileto.feature_pdf_tools.domain.repository.PdfToolsRepository
import javax.inject.Inject

/**
 * UseCase برای دریافت اطلاعات فایل PDF
 */
class GetPdfInfoUseCase @Inject constructor(
    private val repository: PdfToolsRepository
) {
    
    /**
     * دریافت اطلاعات فایل PDF
     * @param filePath مسیر فایل PDF
     * @return اطلاعات فایل یا خطا
     */
    suspend operator fun invoke(filePath: String): Result<PdfInfo> {
        if (filePath.isBlank()) {
            return Result.failure(IllegalArgumentException("مسیر فایل نمی‌تواند خالی باشد"))
        }
        
        return try {
            repository.getPdfInfo(filePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
