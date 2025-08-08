package ir.dekot.fileto.feature_create_pdf.domain.usecase

import ir.dekot.fileto.feature_create_pdf.domain.model.ImageItem
import ir.dekot.fileto.feature_create_pdf.domain.repository.PdfCreationRepository
import javax.inject.Inject

class CreatePdfFromImagesUseCase @Inject constructor(
    private val repository: PdfCreationRepository
) {
    suspend operator fun invoke(images: List<ImageItem>, fileName: String): Result<String> {
        if (images.isEmpty()) {
            return Result.failure(IllegalArgumentException("لیست تصاویر نمی‌تواند خالی باشد."))
        }
        if (fileName.isBlank()) {
            return Result.failure(IllegalArgumentException("نام فایل نمی‌تواند خالی باشد."))
        }
        // اطمینان از اینکه نام فایل پسوند .pdf را دارد
        val finalFileName = if (fileName.endsWith(".pdf", ignoreCase = true)) fileName else "$fileName.pdf"

        return repository.createPdfFromImages(images, finalFileName)
    }
}