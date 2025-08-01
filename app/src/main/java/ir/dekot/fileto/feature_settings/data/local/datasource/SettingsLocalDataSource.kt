package ir.dekot.fileto.feature_settings.data.local.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ir.dekot.fileto.feature_settings.domain.model.Theme
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dekot.fileto.feature_settings.domain.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsLocalDataSource @Inject constructor(@param:ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme_preference")
        val LANGUAGE = stringPreferencesKey("language_preference")
    }

    val themeFlow: Flow<Theme> = context.dataStore.data.map { preferences ->
        Theme.valueOf(preferences[PreferencesKeys.THEME] ?: Theme.SYSTEM.name)
    }

    suspend fun setTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    val languageFlow: Flow<Language> = context.dataStore.data.map { preferences ->
        Language.valueOf(preferences[PreferencesKeys.LANGUAGE] ?: Language.PERSIAN.name)
    }

    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.name
        }
    }
}