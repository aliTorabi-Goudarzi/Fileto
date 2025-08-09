package ir.dekot.fileto.feature_pdf_tools.data.local.datasource

import ir.dekot.fileto.feature_pdf_tools.data.local.dto.PdfInfoDto
import ir.dekot.fileto.feature_pdf_tools.data.local.dto.SplitResultDto
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitOptions

/**
 * DataSource برای عملیات مربوط به PDF
 * این interface تمام عملیات سطح پایین PDF را تعریف می‌کند
 */
interface PdfDataSource {
    
    /**
     * استخراج اطلاعات فایل PDF
     */
    suspend fun extractPdfInfo(filePath: String): PdfInfoDto
    
    /**
     * تقسیم فایل PDF بر اساس تنظیمات
     */
    suspend fun splitPdf(
        inputFilePath: String,
        splitOptions: SplitOptions,
        outputDirectory: String
    ): SplitResultDto
    
    /**
     * ادغام چندین فایل PDF
     */
    suspend fun mergePdfs(
        inputFilePaths: List<String>,
        outputFilePath: String
    ): String
    
    /**
     * بررسی معتبر بودن فایل PDF
     */
    suspend fun validatePdfFile(filePath: String): Boolean
    
    /**
     * دریافت نام فایل از URI
     */
    fun getFileNameFromUri(uriPath: String): String?
    
    /**
     * دریافت حجم فایل از URI
     */
    fun getFileSizeFromUri(uriPath: String): Long?
}
