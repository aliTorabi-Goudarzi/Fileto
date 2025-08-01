package ir.dekot.fileto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.dekot.fileto.feature_settings.domain.model.Theme
import ir.dekot.fileto.feature_settings.domain.usecase.GetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dekot.fileto.feature_settings.domain.model.Language
import ir.dekot.fileto.feature_settings.domain.usecase.GetLanguageUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    getLanguageUseCase: GetLanguageUseCase
) : ViewModel() {
    val theme = getThemeUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Theme.SYSTEM)

    val language = getLanguageUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Language.PERSIAN)
}