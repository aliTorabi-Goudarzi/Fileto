package ir.dekot.fileto.feature_compress.data.local.datasource

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.ReaderProperties
import com.itextpdf.kernel.pdf.WriterProperties
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dekot.fileto.feature_compress.data.local.entity.CompressionProfileDto
import ir.dekot.fileto.feature_compress.data.local.entity.CompressionSettingsDto
import javax.inject.Inject
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject
import com.itextpdf.kernel.pdf.PdfName
import androidx.core.graphics.scale
import kotlin.math.roundToInt

class PdfLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun compressPdfFile(
        sourceUri: Uri,
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

    private fun processImages(pdfDoc: PdfDocument, imageQuality: Int, downscaleResolution: Int) {
        for (i in 1..pdfDoc.numberOfPages) {
            val page = pdfDoc.getPage(i)
            val resources = page.resources
            val xobjects = resources.getResource(PdfName.XObject)
            if (xobjects == null || !xobjects.isDictionary) {
                continue
            }

            val xobjectDict = xobjects
            val keys = xobjectDict.keySet()
            for (key in keys) {
                val xobjectStream = xobjectDict.getAsStream(key)

                if (xobjectStream != null && PdfName.Image.equals(xobjectStream.getAsName(PdfName.Subtype))) {
                    try {
                        val imageObject = PdfImageXObject(xobjectStream)
                        val imageBytes = imageObject.imageBytes
                        var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        if (bitmap != null) {
                            // کاهش رزولوشن تصویر
                            bitmap = getScaledBitmap(bitmap, downscaleResolution)

                            val baos = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, baos)
                            val newImageBytes = baos.toByteArray()

                            val newImageData = ImageDataFactory.create(newImageBytes)
                            val newImage = PdfImageXObject(newImageData)

                            xobjectDict.put(key, newImage.pdfObject)
                            bitmap.recycle()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getScaledBitmap(bitmap: Bitmap, targetDpi: Int): Bitmap {
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
}