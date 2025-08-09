package ir.dekot.fileto.feature_pdf_tools.presentation.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.MergeType
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dekot.fileto.feature_pdf_tools.domain.model.PdfTool
import ir.dekot.fileto.feature_pdf_tools.domain.model.PdfToolType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel برای مدیریت صفحه ابزارهای PDF
 */
@HiltViewModel
class PdfToolsViewModel @Inject constructor() : ViewModel() {

    private val _availableTools = MutableStateFlow<List<PdfTool>>(emptyList())
    val availableTools: StateFlow<List<PdfTool>> = _availableTools.asStateFlow()

    init {
        loadAvailableTools()
    }

    /**
     * بارگذاری لیست ابزارهای موجود
     */
    private fun loadAvailableTools(): Unit {
        val tools = PdfToolType.entries.map { toolType ->
            PdfTool(
                id = toolType.id,
                titleRes = toolType.titleRes,
                descriptionRes = toolType.descriptionRes,
                iconRes = getIconForTool(toolType),
                route = toolType.route
            )
        }
        _availableTools.value = tools
    }

    /**
     * تعیین آیکون مناسب برای هر ابزار
     */
    private fun getIconForTool(toolType: PdfToolType): Int {
        return when (toolType) {
            PdfToolType.SPLIT -> Icons.Default.ContentCut.hashCode()
            PdfToolType.MERGE -> Icons.AutoMirrored.Filled.MergeType.hashCode()
        }
    }
}
