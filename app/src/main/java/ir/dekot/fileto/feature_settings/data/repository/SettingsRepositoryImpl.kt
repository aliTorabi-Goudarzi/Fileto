package ir.dekot.fileto.feature_settings.data.repository

import ir.dekot.fileto.feature_settings.data.local.datasource.SettingsLocalDataSource
import ir.dekot.fileto.feature_settings.domain.model.Language
import ir.dekot.fileto.feature_settings.domain.model.Theme
import ir.dekot.fileto.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {
    override fun getTheme(): Flow<Theme> = localDataSource.themeFlow
    override suspend fun setTheme(theme: Theme) = localDataSource.setTheme(theme)

    override fun getLanguage(): Flow<Language> = localDataSource.languageFlow
    override suspend fun setLanguage(language: Language) = localDataSource.setLanguage(language)
}