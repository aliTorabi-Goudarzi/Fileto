package ir.dekot.fileto.feature_compress.domain.usecase

import android.net.Uri
import ir.dekot.fileto.feature_compress.domain.repository.PdfRepository
import javax.inject.Inject

class GetFileSizeUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke(uri: Uri): Long? {
        return repository.getFileSizeFromUri(uri)
    }
}