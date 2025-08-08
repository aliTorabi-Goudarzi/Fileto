package ir.dekot.fileto.feature_create_pdf.data.local.datasource

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.AreaBreak
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.properties.AreaBreakType
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dekot.fileto.feature_create_pdf.domain.model.ImageItem
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class PdfCreationLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    fun createPdfFromImages(images: List<ImageItem>, outputPdfFileName: String): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, outputPdfFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/Fileto")
            }
        }

        val resolver = context.contentResolver
        val destinationUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("Could not create destination file.")
        } else {
            TODO("VERSION.SDK_INT < Q")
        }

        val outputStream = resolver.openOutputStream(destinationUri)
            ?: throw Exception("Could not open output stream from URI")

        val writer = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)

        try {
            images.forEachIndexed { index, imageItem ->
                val imageUri = imageItem.uri.toUri()

                // 1. خواندن کامل تصویر و اعمال چرخش صحیح بر اساس EXIF
                val correctlyRotatedBitmap = loadAndRotateBitmap(context, imageUri)

                // 2. تعیین جهت صفحه بر اساس ابعاد تصویرِ *اصلاح شده*
                val pageSize = if (correctlyRotatedBitmap.width > correctlyRotatedBitmap.height) {
                    PageSize.A4.rotate() // تصویر افقی
                } else {
                    PageSize.A4 // تصویر عمودی
                }

                if (index > 0) {
                    document.add(AreaBreak(AreaBreakType.NEXT_PAGE))
                }

                // 3. تبدیل بیت‌مپ اصلاح شده به فرمت قابل استفاده برای PDF
                val stream = ByteArrayOutputStream()
                correctlyRotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                val imageData = ImageDataFactory.create(stream.toByteArray())
                val image = Image(imageData)

                // 4. تنظیم حاشیه و اندازه برای هر صفحه به صورت مجزا
                document.setMargins(20f, 20f, 20f, 20f)
                image.scaleToFit(pageSize.width - 40, pageSize.height - 40)

                // 5. محاسبه موقعیت برای مرکز چین کردن دقیق
                val x = (pageSize.width - image.imageScaledWidth) / 2
                val y = (pageSize.height - image.imageScaledHeight) / 2
                image.setFixedPosition(index + 1, x, y)

                document.add(image)

                // 6. آزاد کردن حافظه بیت‌مپ
                correctlyRotatedBitmap.recycle()
            }
        } finally {
            document.close()
            outputStream.close()
        }
        return destinationUri
    }

    /**
     * یک بیت‌مپ از URI می‌خواند و آن را بر اساس متادیتای EXIF می‌چرخاند.
     */
    private fun loadAndRotateBitmap(context: Context, photoUri: Uri): Bitmap {
        val inputStream: InputStream = context.contentResolver.openInputStream(photoUri)
            ?: throw Exception("Cannot open input stream for URI: $photoUri")

        // خواندن کامل بیت‌مپ
        val sourceBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close() // بستن سریع استریم

        // خواندن اطلاعات چرخش از EXIF
        val exifInterface = context.contentResolver.openInputStream(photoUri)?.let { ExifInterface(it) }
        val orientation = exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) ?: ExifInterface.ORIENTATION_NORMAL

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        // ایجاد بیت‌مپ جدید با چرخش صحیح
        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.width, sourceBitmap.height, matrix, true)
    }
}