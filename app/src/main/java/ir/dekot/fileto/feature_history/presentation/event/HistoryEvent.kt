package ir.dekot.fileto.feature_history.presentation.event

import android.content.Intent

sealed class HistoryEvent {
    data class Navigate(val intent: Intent) : HistoryEvent()
}