package ir.dekot.fileto.feature_create_pdf.domain.repository

import ir.dekot.fileto.feature_create_pdf.domain.model.ImageItem

interface PdfCreationRepository {

    /**
     * یک فایل PDF از لیستی از تصاویر می‌سازد.
     * @param images لیست تصاویر برای افزودن به PDF.
     * @param outputPdfFileName نام فایل PDF خروجی.
     * @return یک Result که در صورت موفقیت، مسیر (uri string) فایل ساخته شده
     * و در صورت شکست، یک Exception را برمی‌گرداند.
     */
    suspend fun createPdfFromImages(images: List<ImageItem>, outputPdfFileName: String): Result<String>
}