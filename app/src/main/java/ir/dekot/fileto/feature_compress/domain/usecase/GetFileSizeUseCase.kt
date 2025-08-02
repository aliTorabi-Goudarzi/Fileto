package ir.dekot.fileto.feature_compress.domain.usecase

import ir.dekot.fileto.feature_compress.domain.repository.PdfRepository
import javax.inject.Inject

class GetFileSizeUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke(uriPath: String): Long? { // از Uri به String تغییر کرد
        return repository.getFileSizeFromUri(uriPath)
    }
}