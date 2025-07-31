package ir.dekot.fileto.feature_compress.domain.model

data class CompressionSettings(
    val imageQuality: Int = 80, // کیفیت تصویر از ۰ تا ۱۰۰
    val downscaleResolution: Int = 150, // DPI برای کاهش رزولوشن تصاویر
    val removeMetadata: Boolean = true,
    val useObjectStreamCompression: Boolean = true
)