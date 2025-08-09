package ir.dekot.fileto.feature_pdf_tools.data.local.datasource

import android.content.Context
import androidx.core.net.toUri
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dekot.fileto.feature_pdf_tools.data.local.dto.PdfInfoDto
import ir.dekot.fileto.feature_pdf_tools.data.local.dto.SplitResultDto
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitOptions
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * پیاده‌سازی DataSource با استفاده از iText برای عملیات PDF
 */
class PdfDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : PdfDataSource {

    override suspend fun extractPdfInfo(filePath: String): PdfInfoDto = withContext(Dispatchers.IO) {
        val uri = filePath.toUri()
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("نمی‌توان فایل را باز کرد")

        inputStream.use { stream ->
            val pdfReader = PdfReader(stream)
            val pdfDocument = PdfDocument(pdfReader)

            val fileName = getFileNameFromUri(filePath) ?: "Unknown.pdf"
            val fileSize = getFileSizeFromUri(filePath) ?: 0L
            val totalPages = pdfDocument.numberOfPages

            // استخراج metadata - فعلاً ساده نگه می‌داریم
            val title: String? = null
            val author: String? = null
            val creationDate: Long? = null

            pdfDocument.close()

            PdfInfoDto(
                fileName = fileName,
                filePath = filePath,
                totalPages = totalPages,
                fileSize = fileSize,
                creationDate = creationDate,
                author = author,
                title = title
            )
        }
    }

    override suspend fun splitPdf(
        inputFilePath: String,
        splitOptions: SplitOptions,
        outputDirectory: String
    ): SplitResultDto = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val uri = inputFilePath.toUri()
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("نمی‌توان فایل ورودی را باز کرد")

        val outputDir = File(outputDirectory)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val outputFiles = mutableListOf<String>()
        val fileName = getFileNameFromUri(inputFilePath) ?: "split"
        val baseName = fileName.substringBeforeLast(".")

        inputStream.use { stream ->
            val pdfReader = PdfReader(stream)
            val sourcePdf = PdfDocument(pdfReader)
            val totalPages = sourcePdf.numberOfPages
            val originalFileSize = getFileSizeFromUri(inputFilePath) ?: 0L

            when (splitOptions.splitType) {
                SplitType.EACH_PAGE -> {
                    // هر صفحه در فایل جداگانه
                    for (pageNum in 1..totalPages) {
                        val outputFile = File(outputDir, "${baseName}_page_${pageNum}.pdf")
                        createSinglePagePdf(sourcePdf, pageNum, outputFile)
                        outputFiles.add(outputFile.absolutePath)
                    }
                }
                
                SplitType.BY_PAGES -> {
                    // تقسیم بر اساس تعداد صفحات
                    val pagesPerSplit = splitOptions.pagesPerSplit
                    var currentPage = 1
                    var partNumber = 1
                    
                    while (currentPage <= totalPages) {
                        val endPage = minOf(currentPage + pagesPerSplit - 1, totalPages)
                        val outputFile = File(outputDir, "${baseName}_part_${partNumber}_pages_${currentPage}-${endPage}.pdf")
                        
                        createMultiPagePdf(sourcePdf, currentPage, endPage, outputFile)
                        outputFiles.add(outputFile.absolutePath)
                        
                        currentPage = endPage + 1
                        partNumber++
                    }
                }
                
                SplitType.BY_RANGE -> {
                    // تقسیم بر اساس بازه‌های مشخص شده
                    splitOptions.pageRanges.forEachIndexed { index, range ->
                        val pageNumbers = parsePageRange(range, totalPages)
                        if (pageNumbers.isNotEmpty()) {
                            val outputFile = File(outputDir, "${baseName}_range_${index + 1}_($range).pdf")
                            createRangePdf(sourcePdf, pageNumbers, outputFile)
                            outputFiles.add(outputFile.absolutePath)
                        }
                    }
                }
            }

            sourcePdf.close()
        }

        val processingTime = System.currentTimeMillis() - startTime
        val totalOutputSize = outputFiles.sumOf { File(it).length() }

        SplitResultDto(
            outputFilePaths = outputFiles,
            originalTotalPages = getTotalPagesFromUri(inputFilePath),
            splitCount = outputFiles.size,
            processingTimeMs = processingTime,
            originalFileSize = getFileSizeFromUri(inputFilePath) ?: 0L,
            totalOutputSize = totalOutputSize
        )
    }

    /**
     * ایجاد PDF با یک صفحه
     */
    private fun createSinglePagePdf(sourcePdf: PdfDocument, pageNumber: Int, outputFile: File) {
        val outputStream = FileOutputStream(outputFile)
        outputStream.use { stream ->
            val pdfWriter = PdfWriter(stream)
            val targetPdf = PdfDocument(pdfWriter)
            
            sourcePdf.copyPagesTo(pageNumber, pageNumber, targetPdf)
            targetPdf.close()
        }
    }

    /**
     * ایجاد PDF با چندین صفحه متوالی
     */
    private fun createMultiPagePdf(sourcePdf: PdfDocument, startPage: Int, endPage: Int, outputFile: File) {
        val outputStream = FileOutputStream(outputFile)
        outputStream.use { stream ->
            val pdfWriter = PdfWriter(stream)
            val targetPdf = PdfDocument(pdfWriter)
            
            sourcePdf.copyPagesTo(startPage, endPage, targetPdf)
            targetPdf.close()
        }
    }

    /**
     * ایجاد PDF با صفحات غیرمتوالی
     */
    private fun createRangePdf(sourcePdf: PdfDocument, pageNumbers: List<Int>, outputFile: File) {
        val outputStream = FileOutputStream(outputFile)
        outputStream.use { stream ->
            val pdfWriter = PdfWriter(stream)
            val targetPdf = PdfDocument(pdfWriter)
            
            pageNumbers.forEach { pageNum ->
                sourcePdf.copyPagesTo(pageNum, pageNum, targetPdf)
            }
            
            targetPdf.close()
        }
    }

    /**
     * تجزیه بازه صفحات (مثال: "1-5" -> [1,2,3,4,5], "7" -> [7])
     */
    private fun parsePageRange(range: String, maxPages: Int): List<Int> {
        val trimmedRange = range.trim()
        val pageNumbers = mutableListOf<Int>()
        
        if (trimmedRange.contains("-")) {
            // بازه (مثال: "1-5")
            val parts = trimmedRange.split("-")
            if (parts.size == 2) {
                val start = parts[0].toIntOrNull() ?: return emptyList()
                val end = parts[1].toIntOrNull() ?: return emptyList()
                
                if (start > 0 && end > 0 && start <= end && start <= maxPages) {
                    val actualEnd = minOf(end, maxPages)
                    for (i in start..actualEnd) {
                        pageNumbers.add(i)
                    }
                }
            }
        } else {
            // صفحه تکی (مثال: "7")
            val pageNum = trimmedRange.toIntOrNull()
            if (pageNum != null && pageNum > 0 && pageNum <= maxPages) {
                pageNumbers.add(pageNum)
            }
        }
        
        return pageNumbers
    }

    override suspend fun mergePdfs(inputFilePaths: List<String>, outputFilePath: String): String {
        // فعلاً پیاده‌سازی ساده - بعداً تکمیل می‌شود
        return outputFilePath
    }

    override suspend fun validatePdfFile(filePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = filePath.toUri()
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext false
            
            inputStream.use { stream ->
                val pdfReader = PdfReader(stream)
                val pdfDocument = PdfDocument(pdfReader)
                val isValid = pdfDocument.numberOfPages > 0
                pdfDocument.close()
                isValid
            }
        } catch (_: Exception) {
            false
        }
    }

    override fun getFileNameFromUri(uriPath: String): String? {
        return try {
            val uri = uriPath.toUri()
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        return it.getString(nameIndex)
                    }
                }
            }
            uri.lastPathSegment
        } catch (_: Exception) {
            null
        }
    }

    override fun getFileSizeFromUri(uriPath: String): Long? {
        return try {
            val uri = uriPath.toUri()
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    if (sizeIndex >= 0) {
                        return it.getLong(sizeIndex)
                    }
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    /**
     * دریافت تعداد صفحات از URI
     */
    private suspend fun getTotalPagesFromUri(filePath: String): Int = withContext(Dispatchers.IO) {
        try {
            val uri = filePath.toUri()
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext 0
            
            inputStream.use { stream ->
                val pdfReader = PdfReader(stream)
                val pdfDocument = PdfDocument(pdfReader)
                val pages = pdfDocument.numberOfPages
                pdfDocument.close()
                pages
            }
        } catch (_: Exception) {
            0
        }
    }
}
