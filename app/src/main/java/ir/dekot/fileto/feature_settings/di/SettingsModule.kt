package ir.dekot.fileto.feature_settings.di

import ir.dekot.fileto.feature_settings.data.local.datasource.SettingsLocalDataSource
import ir.dekot.fileto.feature_settings.data.repository.SettingsRepositoryImpl
import ir.dekot.fileto.feature_settings.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    @Singleton
    fun provideSettingsRepository(dataSource: SettingsLocalDataSource): SettingsRepository {
        return SettingsRepositoryImpl(dataSource)
    }
}
