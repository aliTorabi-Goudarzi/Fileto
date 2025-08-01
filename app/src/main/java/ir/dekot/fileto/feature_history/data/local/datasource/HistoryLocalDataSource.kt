package ir.dekot.fileto.feature_history.data.local.datasource

import ir.dekot.fileto.feature_history.data.local.dao.HistoryDao
import ir.dekot.fileto.feature_history.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryLocalDataSource @Inject constructor(
    private val dao: HistoryDao
) {
    fun getHistory(): Flow<List<HistoryEntity>> {
        return dao.getHistory()
    }

    suspend fun insertHistory(history: HistoryEntity) {
        dao.insertHistory(history)
    }

    // متد فراموش شده اضافه شد
    suspend fun updateStarStatus(id: Int, isStarred: Boolean) {
        dao.updateStarStatus(id, isStarred)
    }

    // متد فراموش شده اضافه شد
    suspend fun deleteHistoryById(id: Int) {
        dao.deleteHistoryById(id)
    }
}
