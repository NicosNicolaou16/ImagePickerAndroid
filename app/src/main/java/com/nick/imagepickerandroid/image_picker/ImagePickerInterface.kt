package com.nick.imagepickerandroid.image_picker

import android.graphics.Bitmap
import android.net.Uri

interface ImagePickerInterface {

    fun onBitmap(bitmap: Bitmap?, uri: Uri?) {}
    fun onMultipleBitmaps(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?,
    ) {
    }
}