package ir.dekot.fileto.feature_compress.data.local.entity

data class CompressionSettingsDto(
    val imageQuality: Int,
    val downscaleResolution: Int,
    val removeMetadata: Boolean,
    val useObjectStreamCompression: Boolean
)