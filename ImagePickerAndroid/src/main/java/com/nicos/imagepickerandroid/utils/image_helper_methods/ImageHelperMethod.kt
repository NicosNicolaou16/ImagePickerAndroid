package com.nicos.imagepickerandroid.utils.image_helper_methods

import android.graphics.Bitmap
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.ByteArrayOutputStream

class ImageHelperMethod {

    fun convertBitmapToBase64(bitmap: Bitmap?) = flow {
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

    fun convertListOfBitmapsToListOfBase64(bitmapList: MutableList<Bitmap>?) = flow {
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
}