package ir.dekot.fileto.feature_pdf_tools.domain.repository

import ir.dekot.fileto.feature_pdf_tools.domain.model.PdfInfo
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitOptions
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitResult

/**
 * Repository برای عملیات ابزارهای PDF
 */
interface PdfToolsRepository {
    
    /**
     * دریافت اطلاعات فایل PDF
     */
    suspend fun getPdfInfo(filePath: String): Result<PdfInfo>
    
    /**
     * تقسیم فایل PDF بر اساس تنظیمات داده شده
     */
    suspend fun splitPdf(
        inputFilePath: String,
        splitOptions: SplitOptions,
        outputDirectory: String
    ): Result<SplitResult>
    
    /**
     * ادغام چندین فایل PDF
     */
    suspend fun mergePdfs(
        inputFilePaths: List<String>,
        outputFilePath: String
    ): Result<String>
    
    /**
     * دریافت نام فایل از مسیر URI
     */
    fun getFileNameFromUri(uriPath: String): String?
    
    /**
     * دریافت حجم فایل از مسیر URI
     */
    fun getFileSizeFromUri(uriPath: String): Long?
}
