package com.nicos.imagepickerandroid.utils.image_helper_methods

import android.graphics.Bitmap
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream

class ImageHelperMethods {

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
                    ) ?: null
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
                    Bitmap.createScaledBitmap(
                        bitmap,
                        scaleBitmapModel.width,
                        scaleBitmapModel.height,
                        true
                    )
                }
                emit(bitmapAfterScaleList)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(null)
            }
        }.flowOn(Dispatchers.Default)
}