package ir.dekot.fileto.feature_history.di

import ir.dekot.fileto.feature_history.data.local.FiletoDatabase
import ir.dekot.fileto.feature_history.data.repository.HistoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.dekot.fileto.feature_history.data.local.datasource.HistoryLocalDataSource
import ir.dekot.fileto.feature_history.domain.repository.HistoryRepository
import javax.inject.Singleton

    @Module
    @InstallIn(SingletonComponent::class)
    object HistoryModule {
        @Provides
        @Singleton
        fun provideHistoryRepository(dataSource: HistoryLocalDataSource): HistoryRepository {
            return HistoryRepositoryImpl(dataSource)
        }

        @Provides
        @Singleton
        fun provideHistoryLocalDataSource(db: FiletoDatabase): HistoryLocalDataSource {
            return HistoryLocalDataSource(db.historyDao)
        }
    }