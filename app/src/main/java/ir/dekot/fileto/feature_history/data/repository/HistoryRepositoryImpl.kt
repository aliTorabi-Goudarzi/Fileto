package ir.dekot.fileto.feature_history.data.repository

import ir.dekot.fileto.feature_history.data.local.datasource.HistoryLocalDataSource
import ir.dekot.fileto.feature_history.data.mapper.toDomain
import ir.dekot.fileto.feature_history.data.mapper.toEntity
import ir.dekot.fileto.feature_history.domain.model.HistoryItem
import ir.dekot.fileto.feature_history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val dataSource: HistoryLocalDataSource
) : HistoryRepository {
    override fun getHistory(): Flow<List<HistoryItem>> {
        return dataSource.getHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addHistoryItem(item: HistoryItem) {
        dataSource.insertHistory(item.toEntity())
    }

    override suspend fun toggleStarStatus(id: Int, isStarred: Boolean) {
        dataSource.updateStarStatus(id, isStarred)
    }

    override suspend fun deleteHistoryItem(id: Int) {
        dataSource.deleteHistoryById(id)
    }
}