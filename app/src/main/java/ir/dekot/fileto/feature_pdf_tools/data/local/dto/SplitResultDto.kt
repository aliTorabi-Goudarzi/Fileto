package ir.dekot.fileto.feature_pdf_tools.data.local.dto

/**
 * DTO برای نتیجه تقسیم PDF در لایه Data
 */
data class SplitResultDto(
    val outputFilePaths: List<String>,
    val originalTotalPages: Int,
    val splitCount: Int,
    val processingTimeMs: Long,
    val originalFileSize: Long,
    val totalOutputSize: Long
)
