package ir.dekot.fileto.feature_pdf_tools.data.mapper

import ir.dekot.fileto.feature_pdf_tools.data.local.dto.PdfInfoDto
import ir.dekot.fileto.feature_pdf_tools.data.local.dto.SplitResultDto
import ir.dekot.fileto.feature_pdf_tools.domain.model.PdfInfo
import ir.dekot.fileto.feature_pdf_tools.domain.model.SplitResult

/**
 * Mapper برای تبدیل بین DTO ها و Domain Models
 */
object PdfToolsMapper {
    
    /**
     * تبدیل PdfInfoDto به PdfInfo
     */
    fun mapPdfInfoDtoToDomain(dto: PdfInfoDto): PdfInfo {
        return PdfInfo(
            fileName = dto.fileName,
            filePath = dto.filePath,
            totalPages = dto.totalPages,
            fileSize = dto.fileSize
        )
    }
    
    /**
     * تبدیل SplitResultDto به SplitResult
     */
    fun mapSplitResultDtoToDomain(dto: SplitResultDto): SplitResult {
        return SplitResult(
            outputFiles = dto.outputFilePaths,
            totalPages = dto.originalTotalPages,
            splitCount = dto.splitCount
        )
    }
}
