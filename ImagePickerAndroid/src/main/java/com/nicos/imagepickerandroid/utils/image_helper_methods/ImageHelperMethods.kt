package com.nicos.imagepickerandroid.utils.image_helper_methods

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.graphics.scale
import com.nicos.imagepickerandroid.model.DecodedImages
import com.nicos.imagepickerandroid.utils.extensions.getUriWithFileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class ImageHelperMethods {

    companion object {
        private const val PATTERN_DATE_FORMAT: String = "yyyyMMdd_HHmmss_SSS"
    }

    /**
     * This make the conversion from Uri to Bitmap
     * */
    internal fun convertUriToBitmap(
        contentResolver: ContentResolver,
        uri: Uri?
    ): Bitmap? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source: ImageDecoder.Source? =
                uri?.let { ImageDecoder.createSource(contentResolver, it) }
            source?.let {
                ImageDecoder.decodeBitmap(it) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }
            }
        }
    }

    /**
     * This method return the image from Intent when take with Camera
     * @param intent pass intent instance
     * */
    internal fun getExtrasBitmapAccordingWithSDK(intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.extras?.getParcelable(
            "data",
            Bitmap::class.java
        ) else intent.extras?.get("data") as? Bitmap

    /**
     * This method converted a bitmap to a base64 value
     * @param bitmap gives a bitmap
     * */
    internal fun convertBitmapToBase64(bitmap: Bitmap?) = flow {
        if (bitmap != null) {
            try {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val bytes: ByteArray = byteArrayOutputStream.toByteArray()
                emit(Base64.encodeToString(bytes, Base64.NO_WRAP) ?: null)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(null)
            }
        } else
            emit(null)
    }.flowOn(Dispatchers.Default)

    /**
     * This method converted a list of bitmaps to a list of base64 values
     * @param bitmapList list of bitmap
     * */
    internal fun convertListOfBitmapsToListOfBase64(bitmapList: MutableList<Bitmap>?) = flow {
        if (bitmapList != null) {
            try {
                val bitmapListToBase64List = mutableListOf<String>()
                bitmapList.forEach { bitmap ->
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val bytes: ByteArray = byteArrayOutputStream.toByteArray()
                    bitmapListToBase64List.add(Base64.encodeToString(bytes, Base64.NO_WRAP))
                }
                emit(bitmapListToBase64List)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(null)
            }
        } else
            emit(null)
    }.flowOn(Dispatchers.Default)

    /**
     * The method is using to change the scale of bitmap
     * @param bitmap gives a bitmap
     * @param scaleBitmapModel set height and width for given bitmap
     * */
    internal fun scaleBitmap(bitmap: Bitmap?, scaleBitmapModel: ScaleBitmapModel) = flow {
        if (bitmap != null) {
            try {
                emit(
                    bitmap.scale(scaleBitmapModel.width, scaleBitmapModel.height)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(null)
            }
        } else
            emit(null)
    }.flowOn(Dispatchers.Default)

    /**
     * The method is using to change the scale of bitmap
     * @param bitmapList gives a bitmap list
     * @param scaleBitmapModel set height and width for given bitmap
     * */
    internal fun scaleBitmapList(
        bitmapList: MutableList<Bitmap>,
        scaleBitmapModel: ScaleBitmapModel
    ) =
        flow {
            try {
                val bitmapAfterScaleList = mutableListOf<Bitmap>()
                bitmapList.forEach { bitmap ->
                    bitmapAfterScaleList.add(
                        bitmap.scale(scaleBitmapModel.width, scaleBitmapModel.height)
                    )
                }
                emit(bitmapAfterScaleList)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(null)
            }
        }.flowOn(Dispatchers.Default)

    internal fun getUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val file = createImageFile(context)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return file.getUriWithFileProvider(context)
    }

    internal fun createImageFile(context: Context): File {
        val timestamp = SimpleDateFormat(PATTERN_DATE_FORMAT, Locale.getDefault()).format(
            Date()
        )
        val fileName = "${timestamp}.jpg"
        Log.d("rewewrwr", fileName)
        return File(context.cacheDir, fileName)
    }

    /**
     * Decodes each Uri into a Bitmap, keeping Uris and Bitmaps paired.
     * A Uri that fails to decode is skipped together with its Bitmap, so the two lists never drift apart.
     * Call this from a background dispatcher (it reads files).
     * @param contentResolver content resolver from Activity/Context
     * @param uris list of uris returned by the picker
     * */
    internal fun decodeUrisToBitmaps(
        contentResolver: ContentResolver,
        uris: List<Uri>,
    ): DecodedImages {
        val validUris = mutableListOf<Uri>()
        val bitmaps = mutableListOf<Bitmap>()
        uris.forEach { uri ->
            val bitmap = try {
                convertUriToBitmap(contentResolver = contentResolver, uri = uri)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            if (bitmap != null) {
                validUris.add(uri)
                bitmaps.add(bitmap)
            }
        }
        return DecodedImages(uris = validUris, bitmaps = bitmaps)
    }
}