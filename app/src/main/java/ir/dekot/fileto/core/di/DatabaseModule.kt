package ir.dekot.fileto.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.dekot.fileto.feature_history.data.local.FiletoDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFiletoDatabase(@ApplicationContext context: Context): FiletoDatabase {
        return Room.databaseBuilder(
            context,
            FiletoDatabase::class.java,
            "fileto_db"
        )
            // در پروژه‌های واقعی باید از Migration استفاده کرد.
            // این گزینه در صورت تغییر نسخه، دیتابیس را حذف و دوباره می‌سازد.
            .fallbackToDestructiveMigration(false)
            .build()
    }
}