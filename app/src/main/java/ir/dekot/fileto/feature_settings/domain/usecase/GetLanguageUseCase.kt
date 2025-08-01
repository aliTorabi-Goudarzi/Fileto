package ir.dekot.fileto.feature_settings.domain.usecase

import ir.dekot.fileto.feature_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(private val repository: SettingsRepository) {
    operator fun invoke() = repository.getLanguage()
}