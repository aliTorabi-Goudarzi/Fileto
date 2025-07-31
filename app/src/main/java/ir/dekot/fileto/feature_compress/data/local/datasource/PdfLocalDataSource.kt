package ir.dekot.fileto.feature_compress.data.local.datasource

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.ReaderProperties
import com.itextpdf.kernel.pdf.WriterProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dekot.fileto.feature_compress.data.local.entity.CompressionProfileDto
import ir.dekot.fileto.feature_compress.data.local.entity.CompressionSettingsDto
import javax.inject.Inject

class PdfLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    // این متد اکنون Uri فایل مقصد را برمی‌گرداند
    fun compressPdfFile(
        sourceUri: Uri,
        originalFileName: String,
        profile: CompressionProfileDto,
        customSettings: CompressionSettingsDto?
    ): Uri {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
            ?: throw Exception("Could not open input stream from URI")

        // ساخت نام فایل جدید
        val newFileName = "compressed_${originalFileName}"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/Fileto")
            }
        }

        // ایجاد فایل در پوشه دانلودها با استفاده از MediaStore
        val resolver = context.contentResolver
        val destinationUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("Could not create destination file.")
        } else {
            TODO("VERSION.SDK_INT < Q")
        }

        val outputStream = resolver.openOutputStream(destinationUri)
            ?: throw Exception("Could not open output stream from URI")

        val reader = PdfReader(inputStream, ReaderProperties())
        val writerProperties = WriterProperties().setFullCompressionMode(true)
        // TODO: منطق پیشرفته فشرده‌سازی اینجا پیاده‌سازی می‌شود

        val pdfDoc = PdfDocument(reader, PdfWriter(outputStream, writerProperties))

        pdfDoc.close()
        inputStream.close()
        outputStream.close()

        return destinationUri
    }
}