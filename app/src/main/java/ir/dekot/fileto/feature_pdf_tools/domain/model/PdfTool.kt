package ir.dekot.fileto.feature_pdf_tools.domain.model

/**
 * مدل ابزارهای PDF که در صفحه اصلی ابزارها نمایش داده می‌شود
 */
data class PdfTool(
    val id: String,
    val titleRes: Int, // Resource ID برای عنوان
    val descriptionRes: Int, // Resource ID برای توضیحات
    val iconRes: Int, // Resource ID برای آیکون
    val route: String // مسیر navigation
)

/**
 * انواع مختلف ابزارهای PDF
 */
enum class PdfToolType(
    val id: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val route: String
) {
    SPLIT(
        id = "split_pdf",
        titleRes = ir.dekot.fileto.R.string.split_pdf,
        descriptionRes = ir.dekot.fileto.R.string.split_pdf_description,
        route = "split_pdf_screen"
    ),
    MERGE(
        id = "merge_pdf", 
        titleRes = ir.dekot.fileto.R.string.merge_pdf,
        descriptionRes = ir.dekot.fileto.R.string.merge_pdf_description,
        route = "merge_pdf_screen"
    )
}
