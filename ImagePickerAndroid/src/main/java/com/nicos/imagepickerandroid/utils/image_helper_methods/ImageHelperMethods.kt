package com.nicos.imagepickerandroid.utils.image_helper_methods

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

internal class ImageHelperMethods {

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
            source?.let { ImageDecoder.decodeBitmap(it) }
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

    internal fun convertBitmapToBase64(bitmap: Bitmap?) = flow {
        if (bitmap != null) {
            try {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val bytes: ByteArray = byteArrayOutputStream.toByteArray()
                emit(Base64.encodeToString(bytes, Base64.DEFAULT) ?: null)
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
                    bitmapListToBase64List.add(Base64.encodeToString(bytes, Base64.DEFAULT))
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
                    Bitmap.createScaledBitmap(
                        bitmap,
                        scaleBitmapModel.width,
                        scaleBitmapModel.height,
                        true
                    )
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
                        Bitmap.createScaledBitmap(
                            bitmap,
                            scaleBitmapModel.width,
                            scaleBitmapModel.height,
                            true
                        )
                    )
                }
                emit(bitmapAfterScaleList)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(null)
            }
        }.flowOn(Dispatchers.Default)

    internal fun getUriFromBitmap(bitmap: Bitmap): Uri? {
        val file = File.createTempFile("image", ".jpg")
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()
        val fileOutPut = FileOutputStream(file)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        return Uri.fromFile(file)
    }
}