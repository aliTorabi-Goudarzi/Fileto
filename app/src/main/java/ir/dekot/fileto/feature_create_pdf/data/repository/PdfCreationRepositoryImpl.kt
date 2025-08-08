package ir.dekot.fileto.feature_create_pdf.data.repository


import ir.dekot.fileto.feature_create_pdf.data.local.datasource.PdfCreationLocalDataSource
import ir.dekot.fileto.feature_create_pdf.domain.model.ImageItem
import ir.dekot.fileto.feature_create_pdf.domain.repository.PdfCreationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PdfCreationRepositoryImpl @Inject constructor(
    private val localDataSource: PdfCreationLocalDataSource
) : PdfCreationRepository {

    override suspend fun createPdfFromImages(images: List<ImageItem>, outputPdfFileName: String): Result<String> {
        // عملیات فایل سنگین است و باید در یک ترد پس‌زمینه (IO) انجام شود
        return withContext(Dispatchers.IO) {
            try {
                val resultUri = localDataSource.createPdfFromImages(images, outputPdfFileName)
                Result.success(resultUri.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}