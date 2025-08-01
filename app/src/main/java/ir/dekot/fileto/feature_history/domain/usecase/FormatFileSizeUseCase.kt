package ir.dekot.fileto.feature_history.domain.usecase

import java.util.Locale
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.pow

class FormatFileSizeUseCase @Inject constructor() {
    operator fun invoke(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return String.format(Locale.US, "%.2f %s", bytes / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])

    }
}