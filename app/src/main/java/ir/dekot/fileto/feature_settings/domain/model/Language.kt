package ir.dekot.fileto.feature_settings.domain.model

import androidx.annotation.StringRes
import ir.dekot.fileto.R


enum class Language(val code: String, @param:StringRes val displayNameRes: Int) {
    PERSIAN("fa", R.string.language_persian),
    ENGLISH("en", R.string.language_english)
}