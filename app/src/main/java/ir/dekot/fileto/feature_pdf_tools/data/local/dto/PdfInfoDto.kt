package ir.dekot.fileto.feature_pdf_tools.data.local.dto

/**
 * DTO برای اطلاعات فایل PDF در لایه Data
 */
data class PdfInfoDto(
    val fileName: String,
    val filePath: String,
    val totalPages: Int,
    val fileSize: Long,
    val creationDate: Long? = null,
    val author: String? = null,
    val title: String? = null
)
