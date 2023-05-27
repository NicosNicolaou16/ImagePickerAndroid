package com.nicos.imagepickerandroid.image_picker

import android.graphics.Bitmap
import android.net.Uri

interface ImagePickerInterface {
    fun onGalleryImage(bitmap: Bitmap?, uri: Uri?, base64AsString: String?) {}
    fun onCameraImage(bitmap: Bitmap?, base64AsString: String?) {}
    fun onMultipleGalleryImages(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?,
        base64AsStringList: MutableList<String>?
    ) {
    }
}