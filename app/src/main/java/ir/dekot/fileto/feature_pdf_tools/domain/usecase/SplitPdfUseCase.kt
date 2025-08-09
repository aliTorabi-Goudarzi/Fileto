package ir.dekot.fileto.feature_pdf_tools.domain.usecase

import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitOptions
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitResult
import ir.dekot.fileto.feature_pdf_tools.domain.repository.PdfToolsRepository
import javax.inject.Inject

/**
 * UseCase برای تقسیم فایل PDF
 */
class SplitPdfUseCase @Inject constructor(
    private val repository: PdfToolsRepository
) {
    
    /**
     * تقسیم فایل PDF
     * @param inputFilePath مسیر فایل ورودی
     * @param splitOptions تنظیمات تقسیم
     * @param outputDirectory مسیر پوشه خروجی
     * @return نتیجه تقسیم یا خطا
     */
    suspend operator fun invoke(
        inputFilePath: String,
        splitOptions: SplitOptions,
        outputDirectory: String
    ): Result<SplitResult> {
        
        // بررسی معتبر بودن ورودی‌ها
        if (inputFilePath.isBlank()) {
            return Result.failure(IllegalArgumentException("مسیر فایل ورودی نمی‌تواند خالی باشد"))
        }
        
        if (outputDirectory.isBlank()) {
            return Result.failure(IllegalArgumentException("مسیر پوشه خروجی نمی‌تواند خالی باشد"))
        }
        
        // بررسی تنظیمات تقسیم
        val validationResult = validateSplitOptions(splitOptions)
        if (validationResult.isFailure) {
            return Result.failure(validationResult.exceptionOrNull() ?: Exception("خطای اعتبارسنجی"))
        }
        
        return try {
            repository.splitPdf(inputFilePath, splitOptions, outputDirectory)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * اعتبارسنجی تنظیمات تقسیم
     */
    private fun validateSplitOptions(options: SplitOptions): Result<Unit> {
        return when (options.splitType) {
            ir.dekot.fileto.feature_pdf_tools.domain.model.SplitType.BY_PAGES -> {
                if (options.pagesPerSplit <= 0) {
                    Result.failure(IllegalArgumentException("تعداد صفحات باید بیشتر از صفر باشد"))
                } else {
                    Result.success(Unit)
                }
            }
            ir.dekot.fileto.feature_pdf_tools.domain.model.SplitType.BY_RANGE -> {
                if (options.pageRanges.isEmpty()) {
                    Result.failure(IllegalArgumentException("بازه صفحات نمی‌تواند خالی باشد"))
                } else {
                    // اعتبارسنجی فرمت بازه صفحات
                    val isValidRange = options.pageRanges.all { range ->
                        validatePageRange(range)
                    }
                    if (isValidRange) {
                        Result.success(Unit)
                    } else {
                        Result.failure(IllegalArgumentException("فرمت بازه صفحات نامعتبر است"))
                    }
                }
            }
            ir.dekot.fileto.feature_pdf_tools.domain.model.SplitType.EACH_PAGE -> {
                Result.success(Unit)
            }
        }
    }
    
    /**
     * اعتبارسنجی فرمت بازه صفحات (مثال: "1-5", "7", "9-12")
     */
    private fun validatePageRange(range: String): Boolean {
        val trimmedRange = range.trim()
        
        // بررسی صفحه تکی (مثال: "7")
        if (trimmedRange.matches(Regex("\\d+"))) {
            return trimmedRange.toIntOrNull() != null && trimmedRange.toInt() > 0
        }
        
        // بررسی بازه (مثال: "1-5")
        if (trimmedRange.matches(Regex("\\d+-\\d+"))) {
            val parts = trimmedRange.split("-")
            if (parts.size == 2) {
                val start = parts[0].toIntOrNull()
                val end = parts[1].toIntOrNull()
                return start != null && end != null && start > 0 && end > 0 && start <= end
            }
        }
        
        return false
    }
}
