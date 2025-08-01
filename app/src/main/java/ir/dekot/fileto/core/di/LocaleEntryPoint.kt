package ir.dekot.fileto.core.di

import ir.dekot.fileto.feature_settings.domain.repository.SettingsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface LocaleEntryPoint {
    fun settingsRepository(): SettingsRepository
}