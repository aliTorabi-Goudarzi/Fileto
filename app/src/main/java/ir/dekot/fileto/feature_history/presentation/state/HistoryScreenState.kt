package ir.dekot.fileto.feature_history.presentation.state

import ir.dekot.fileto.feature_history.presentation.screen.HistoryUiItem

data class HistoryScreenState(
    val isLoading: Boolean = true,
    val historyItems: List<HistoryUiItem> = emptyList()
)