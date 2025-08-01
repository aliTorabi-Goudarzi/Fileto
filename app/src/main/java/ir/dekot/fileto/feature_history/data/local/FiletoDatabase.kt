package ir.dekot.fileto.feature_history.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.dekot.fileto.feature_history.data.local.dao.HistoryDao
import ir.dekot.fileto.feature_history.data.local.entity.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 2) // نسخه دیتابیس به‌روز شد
abstract class FiletoDatabase : RoomDatabase() {
    abstract val historyDao: HistoryDao
}