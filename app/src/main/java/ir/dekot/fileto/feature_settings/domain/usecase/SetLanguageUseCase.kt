package ir.dekot.fileto.feature_settings.domain.usecase

import ir.dekot.fileto.feature_settings.domain.model.Language
import ir.dekot.fileto.feature_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetLanguageUseCase @Inject constructor(private val repository: SettingsRepository) {
    suspend operator fun invoke(language: Language) = repository.setLanguage(language)
}