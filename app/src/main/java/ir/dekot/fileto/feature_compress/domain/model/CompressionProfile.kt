package ir.dekot.fileto.feature_compress.domain.model

// یک فیلد جدید برای نمایش درصد تخمینی کاهش حجم اضافه شده است
enum class CompressionProfile(val displayName: String, val estimatedReduction: String) {
    DEFAULT("پیش‌فرض", "~ 10٪"),
    EBOOK("کتاب الکترونیکی", "~ 40٪"),
    HIGH_QUALITY("کیفیت بالا", "~ 25٪"),
    CUSTOM("سفارشی", "متغیر")
}