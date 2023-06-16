package com.nicos.imagepickerandroid.image_picker

import android.graphics.Bitmap
import android.net.Uri

interface ImagePickerInterface {
    fun onGallerySingleImage(bitmap: Bitmap?, uri: Uri?) {}
    fun onCameraImage(bitmap: Bitmap?) {}
    fun onMultipleGalleryImages(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?
    ) {
    }

    fun onGallerySingleVideo(uri: Uri?) {}

    fun onGallerySingleImageWithBase64Value(bitmap: Bitmap?, uri: Uri?, base64AsString: String?) {}
    fun onCameraImageWithBase64Value(bitmap: Bitmap?, base64AsString: String?) {}
    fun onMultipleGalleryImagesWithBase64Value(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?,
        base64AsStringList: MutableList<String>?
    ) {
    }
}