package ir.dekot.fileto.feature_compress.data.local.datasource.helper

import android.graphics.Bitmap
import androidx.core.graphics.scale
import kotlin.math.roundToInt

internal fun getScaledBitmap(bitmap: Bitmap, targetDpi: Int): Bitmap {
    // ابعاد استاندارد کاغذ A4 به اینچ
    val a4WidthInches = 8.27f
    val a4HeightInches = 11.69f

    // محاسبه حداکثر ابعاد مجاز بر اساس DPI هدف
    val maxWidth = (a4WidthInches * targetDpi).toInt()
    val maxHeight = (a4HeightInches * targetDpi).toInt()

    if (bitmap.width <= maxWidth && bitmap.height <= maxHeight) {
        return bitmap // نیازی به تغییر اندازه نیست
    }

    val widthRatio = maxWidth.toFloat() / bitmap.width
    val heightRatio = maxHeight.toFloat() / bitmap.height
    val ratio = minOf(widthRatio, heightRatio)

    val newWidth = (bitmap.width * ratio).roundToInt()
    val newHeight = (bitmap.height * ratio).roundToInt()

    return bitmap.scale(newWidth, newHeight)
}