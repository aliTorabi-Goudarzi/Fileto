package ir.dekot.fileto.feature_compress.domain.usecase

import android.net.Uri
import ir.dekot.fileto.feature_compress.domain.repository.PdfRepository
import javax.inject.Inject

class GetFileNameUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke(uri: Uri): String? {
        // این UseCase وظیفه ساده‌ای دارد: فراخوانی متد مربوطه از ریپازیتوری
        return repository.getFileNameFromUri(uri)
    }
}