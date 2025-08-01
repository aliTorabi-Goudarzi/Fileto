package ir.dekot.fileto.feature_settings.domain.usecase

import ir.dekot.fileto.feature_settings.domain.model.Theme
import ir.dekot.fileto.feature_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(theme: Theme) = repository.setTheme(theme)
}