package com.nick.imagepickerandroid.image_picker

import android.graphics.Bitmap
import android.net.Uri

interface ImagePickerInterface {

    fun onBitmapGallery(bitmap: Bitmap?, uri: Uri?) {}
    fun onBitmapCamera(bitmap: Bitmap?, uri: Uri?) {}
    fun onMultipleBitmapsGallery(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?,
    ) {
    }
}