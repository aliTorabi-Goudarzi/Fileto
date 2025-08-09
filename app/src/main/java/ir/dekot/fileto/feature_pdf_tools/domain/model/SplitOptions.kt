package ir.dekot.fileto.feature_pdf_tools.domain.model

/**
 * انواع مختلف تقسیم PDF
 */
enum class SplitType {
    EACH_PAGE,      // هر صفحه جداگانه
    BY_PAGES,       // بر اساس تعداد صفحات
    BY_RANGE        // بر اساس بازه صفحات
}

/**
 * تنظیمات تقسیم PDF
 */
data class SplitOptions(
    val splitType: SplitType,
    val pagesPerSplit: Int = 1,        // برای BY_PAGES
    val pageRanges: List<String> = emptyList()  // برای BY_RANGE (مثال: ["1-5", "7", "9-12"])
)

/**
 * نتیجه تقسیم PDF
 */
data class SplitResult(
    val outputFiles: List<String>,     // مسیرهای فایل‌های خروجی
    val totalPages: Int,               // تعداد کل صفحات فایل اصلی
    val splitCount: Int                // تعداد فایل‌های تولید شده
)

/**
 * اطلاعات فایل PDF برای نمایش
 */
data class PdfInfo(
    val fileName: String,
    val filePath: String,
    val totalPages: Int,
    val fileSize: Long
)
