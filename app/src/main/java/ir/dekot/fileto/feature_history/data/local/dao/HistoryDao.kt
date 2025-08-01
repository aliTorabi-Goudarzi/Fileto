package ir.dekot.fileto.feature_history.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.dekot.fileto.feature_history.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("SELECT * FROM historyentity ORDER BY isStarred DESC, timestamp DESC") // موارد ستاره‌دار در ابتدا نمایش داده می‌شوند
    fun getHistory(): Flow<List<HistoryEntity>>

    @Query("UPDATE historyentity SET isStarred = :isStarred WHERE id = :id")
    suspend fun updateStarStatus(id: Int, isStarred: Boolean)

    @Query("DELETE FROM historyentity WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)
}