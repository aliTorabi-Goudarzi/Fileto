package ir.dekot.fileto.feature_compress.data.local.datasource

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.ReaderProperties
import com.itextpdf.kernel.pdf.WriterProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dekot.fileto.feature_compress.data.local.datasource.helper.processImages
import ir.dekot.fileto.feature_compress.data.local.entity.CompressionProfileDto
import ir.dekot.fileto.feature_compress.data.local.entity.CompressionSettingsDto
import javax.inject.Inject
import androidx.core.net.toUri

class PdfLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun compressPdfFile(
        sourceUri: Uri, // ورودی این متد همان Uri باقی می‌ماند
        originalFileName: String,
        profile: CompressionProfileDto,
        customSettings: CompressionSettingsDto?
    ): Uri {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
            ?: throw Exception("Could not open input stream from URI")

        val newFileName = "compressed_${originalFileName}"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName)
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

        val reader = PdfReader(inputStream, ReaderProperties())
        val writerProperties = WriterProperties()

        val useFullCompression = customSettings?.useObjectStreamCompression ?: true
        writerProperties.setFullCompressionMode(useFullCompression)

        val pdfDoc = PdfDocument(reader, PdfWriter(outputStream, writerProperties))

        val imageQuality: Int
        val downscaleResolution: Int

        when (profile) {
            CompressionProfileDto.EBOOK -> {
                imageQuality = 40
                downscaleResolution = 150
            }

            CompressionProfileDto.HIGH_QUALITY -> {
                imageQuality = 75
                downscaleResolution = 300
            }

            CompressionProfileDto.CUSTOM -> {
                imageQuality = customSettings?.imageQuality ?: 80
                downscaleResolution = customSettings?.downscaleResolution ?: 150
            }

            else -> {
                imageQuality = -1
                downscaleResolution = -1
            }
        }

        if (imageQuality != -1) {
            processImages(pdfDoc, imageQuality, downscaleResolution)
        }

        if (customSettings?.removeMetadata == true && profile == CompressionProfileDto.CUSTOM) {
            pdfDoc.documentInfo.title = ""
            pdfDoc.documentInfo.author = ""
            pdfDoc.documentInfo.subject = ""
            pdfDoc.documentInfo.keywords = ""
            pdfDoc.documentInfo.creator = ""
            pdfDoc.catalog.remove(PdfName.Info)
        }

        pdfDoc.close()
        inputStream.close()
        outputStream.close()

        return destinationUri
    }

    /**
     * متد جدید: منطق خواندن نام فایل به اینجا منتقل شد
     */
    fun getFileNameFromUri(uriPath: String): String? {
        return try {
            val uri = uriPath.toUri()
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * متد جدید: منطق خواندن حجم فایل به اینجا منتقل شد
     */
    fun getFileSizeFromUri(uriPath: String): Long? {
        return try {
            val uri = uriPath.toUri()
            // روش اصلی و استاندارد
            context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                        return cursor.getLong(sizeIndex)
                    }
                }
            }
            // روش جایگزین
            context.contentResolver.openFileDescriptor(uri, "r")?.use {
                return it.statSize
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}