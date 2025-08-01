package ir.dekot.fileto.feature_compress.domain.model

import androidx.annotation.StringRes
import ir.dekot.fileto.R

// یک فیلد جدید برای نمایش درصد تخمینی کاهش حجم اضافه شده است
enum class CompressionProfile(@param:StringRes val displayNameRes: Int, @param:StringRes val estimatedReductionRes: Int) {
    DEFAULT(R.string.profile_default, R.string.estimated_reduction_default),
    EBOOK(R.string.profile_ebook, R.string.estimated_reduction_ebook),
    HIGH_QUALITY(R.string.profile_high_quality, R.string.estimated_reduction_high_quality),
    CUSTOM(R.string.profile_custom, R.string.profile_custom_reduction)
}