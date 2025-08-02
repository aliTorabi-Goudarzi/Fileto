package ir.dekot.fileto.feature_compress.data.local.datasource.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject
import java.io.ByteArrayOutputStream

internal fun processImages(pdfDoc: PdfDocument, imageQuality: Int, downscaleResolution: Int) {
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