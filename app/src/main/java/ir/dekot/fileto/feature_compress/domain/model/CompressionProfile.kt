package ir.dekot.fileto.feature_compress.domain.model

// یک فیلد جدید برای نمایش درصد تخمینی کاهش حجم اضافه شده است
enum class CompressionProfile(val displayName: String, val estimatedReduction: String) {
    DEFAULT("پیش‌فرض", "~ ۱۰٪"),
    EBOOK("کتاب الکترونیکی", "~ ۴۰٪"),
    HIGH_QUALITY("کیفیت بالا", "~ ۵٪"),
    CUSTOM("سفارشی", "")
}