package ir.dekot.fileto.feature_pdf_tools.presentation.state

import ir.dekot.fileto.feature_pdf_tools.domain.model.PdfInfo
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitOptions
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitType

/**
 * State برای صفحه تقسیم PDF
 */
data class SplitPdfState(
    val selectedFileUri: String? = null,
    val selectedFileName: String = "",
    val pdfInfo: PdfInfo? = null,
    val isLoadingPdfInfo: Boolean = false,
    val splitOptions: SplitOptions = SplitOptions(SplitType.EACH_PAGE),
    val pageRangeInput: String = "",
    val pagesPerSplitInput: String = "1",
    val isSplitting: Boolean = false,
    val splitProgress: Float = 0f,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val outputFiles: List<String> = emptyList()
)

/**
 * Events برای صفحه تقسیم PDF
 */
sealed class SplitPdfEvent {
    data class FileSelected(val uri: String) : SplitPdfEvent()
    data class SplitTypeChanged(val splitType: SplitType) : SplitPdfEvent()
    data class PageRangeChanged(val range: String) : SplitPdfEvent()
    data class PagesPerSplitChanged(val pages: String) : SplitPdfEvent()
    object StartSplit : SplitPdfEvent()
    object ClearError : SplitPdfEvent()
    object ClearSuccess : SplitPdfEvent()
    object ResetState : SplitPdfEvent()
}
