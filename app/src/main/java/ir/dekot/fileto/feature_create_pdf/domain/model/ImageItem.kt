package ir.dekot.fileto.feature_create_pdf.domain.model

/**
 * نمایانگر یک تصویر انتخاب شده توسط کاربر.
 * @param id یک شناسه منحصر به فرد برای هر آیتم (می‌توان از خود uri استفاده کرد).
 * @param uri مسیر تصویر به صورت رشته برای حفظ استقلال از فریمورک.
 */
data class ImageItem(
    val id: String,
    val uri: String
)