package ir.dekot.fileto.feature_settings.domain.repository

import ir.dekot.fileto.feature_settings.domain.model.Language
import ir.dekot.fileto.feature_settings.domain.model.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getTheme(): Flow<Theme>
    suspend fun setTheme(theme: Theme)

    fun getLanguage(): Flow<Language>
    suspend fun setLanguage(language: Language)
}