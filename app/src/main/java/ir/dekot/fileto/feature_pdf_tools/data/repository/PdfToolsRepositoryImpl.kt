package ir.dekot.fileto.feature_pdf_tools.data.repository

import ir.dekot.fileto.feature_pdf_tools.data.local.datasource.PdfDataSource
import ir.dekot.fileto.feature_pdf_tools.data.mapper.PdfToolsMapper
import ir.dekot.fileto.feature_pdf_tools.domain.model.PdfInfo
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitOptions
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitResult
import ir.dekot.fileto.feature_pdf_tools.domain.repository.PdfToolsRepository
import javax.inject.Inject

/**
 * پیاده‌سازی Repository برای ابزارهای PDF
 * این کلاس فقط مسئول هماهنگی بین لایه Domain و Data است
 */
class PdfToolsRepositoryImpl @Inject constructor(
    private val pdfDataSource: PdfDataSource
) : PdfToolsRepository {

    override suspend fun getPdfInfo(filePath: String): Result<PdfInfo> {
        return try {
            val pdfInfoDto = pdfDataSource.extractPdfInfo(filePath)
            val pdfInfo = PdfToolsMapper.mapPdfInfoDtoToDomain(pdfInfoDto)
            Result.success(pdfInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun splitPdf(
        inputFilePath: String,
        splitOptions: SplitOptions,
        outputDirectory: String
    ): Result<SplitResult> {
        return try {
            // اعتبارسنجی ورودی
            if (!pdfDataSource.validatePdfFile(inputFilePath)) {
                return Result.failure(IllegalArgumentException("فایل PDF معتبر نیست"))
            }
            
            val splitResultDto = pdfDataSource.splitPdf(inputFilePath, splitOptions, outputDirectory)
            val splitResult = PdfToolsMapper.mapSplitResultDtoToDomain(splitResultDto)
            Result.success(splitResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun mergePdfs(
        inputFilePaths: List<String>,
        outputFilePath: String
    ): Result<String> {
        return try {
            // اعتبارسنجی تمام فایل‌های ورودی
            val invalidFiles = inputFilePaths.filter { !pdfDataSource.validatePdfFile(it) }
            if (invalidFiles.isNotEmpty()) {
                return Result.failure(IllegalArgumentException("برخی فایل‌های PDF معتبر نیستند"))
            }
            
            val result = pdfDataSource.mergePdfs(inputFilePaths, outputFilePath)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFileNameFromUri(uriPath: String): String? {
        return pdfDataSource.getFileNameFromUri(uriPath)
    }

    override fun getFileSizeFromUri(uriPath: String): Long? {
        return pdfDataSource.getFileSizeFromUri(uriPath)
    }
}
