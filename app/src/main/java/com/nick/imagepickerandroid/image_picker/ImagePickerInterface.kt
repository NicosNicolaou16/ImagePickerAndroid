package com.nick.imagepickerandroid.image_picker

import android.graphics.Bitmap
import android.net.Uri

interface ImagePickerInterface {
    fun onGalleryImage(bitmap: Bitmap?, uri: Uri?) {}
    fun onCameraImage(bitmap: Bitmap?, uri: Uri?) {}
    fun onMultipleGalleryImages(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?,
    ) {
    }
}