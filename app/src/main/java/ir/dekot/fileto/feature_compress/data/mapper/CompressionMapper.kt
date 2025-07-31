package ir.dekot.fileto.feature_compress.data.mapper

import ir.dekot.fileto.feature_compress.data.local.entity.CompressionProfileDto
import ir.dekot.fileto.feature_compress.data.local.entity.CompressionSettingsDto
import ir.dekot.fileto.feature_compress.domain.model.CompressionProfile
import ir.dekot.fileto.feature_compress.domain.model.CompressionSettings


fun CompressionProfile.toDto(): CompressionProfileDto {
    return when (this) {
        CompressionProfile.DEFAULT -> CompressionProfileDto.DEFAULT
        CompressionProfile.EBOOK -> CompressionProfileDto.EBOOK
        CompressionProfile.HIGH_QUALITY -> CompressionProfileDto.HIGH_QUALITY
        CompressionProfile.CUSTOM -> CompressionProfileDto.CUSTOM
    }
}

fun CompressionSettings.toDto(): CompressionSettingsDto {
    return CompressionSettingsDto(
        imageQuality = this.imageQuality,
        downscaleResolution = this.downscaleResolution,
        removeMetadata = this.removeMetadata,
        useObjectStreamCompression = this.useObjectStreamCompression
    )
}