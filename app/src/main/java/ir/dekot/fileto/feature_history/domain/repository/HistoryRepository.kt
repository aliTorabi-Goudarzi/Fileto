package ir.dekot.fileto.feature_history.domain.repository

import ir.dekot.fileto.feature_history.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(): Flow<List<HistoryItem>>
    suspend fun addHistoryItem(item: HistoryItem)
    suspend fun toggleStarStatus(id: Int, isStarred: Boolean)
    suspend fun deleteHistoryItem(id: Int)
}