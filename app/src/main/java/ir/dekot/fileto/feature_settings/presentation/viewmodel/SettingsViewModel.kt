package ir.dekot.fileto.feature_settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.dekot.fileto.feature_settings.domain.model.Theme
import ir.dekot.fileto.feature_settings.domain.usecase.GetThemeUseCase
import ir.dekot.fileto.feature_settings.domain.usecase.SetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dekot.fileto.feature_settings.domain.model.Language
import ir.dekot.fileto.feature_settings.domain.usecase.GetLanguageUseCase
import ir.dekot.fileto.feature_settings.domain.usecase.SetLanguageUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    getLanguageUseCase: GetLanguageUseCase,
    private val setLanguageUseCase: SetLanguageUseCase
) : ViewModel() {

    val theme = getThemeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Theme.SYSTEM)

    val language = getLanguageUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Language.PERSIAN)

    fun onThemeChange(theme: Theme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }

    fun onLanguageChange(language: Language) {
        viewModelScope.launch {
            setLanguageUseCase(language)
        }
    }
}