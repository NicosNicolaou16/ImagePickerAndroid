package com.nick.imagepickerandroid.image_picker

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImagePicker {

    private var pickImageFromGalleryResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? =
        null
    private var pickMultipleImageFromGalleryResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? =
        null

    /**
     * @param fragmentActivity instance for current Activity (Optional)
     * @param fragment instance for current Fragment (Optional)
     * */
    internal fun pickAnImageFromGallery(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
    ) {
        fragmentActivity?.let {
            pickImageFromGalleryResultLauncher?.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        fragment?.let {
            pickImageFromGalleryResultLauncher?.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }

    /**
     * @param fragmentActivity instance for current Activity (Optional)
     * @param fragment instance for current Fragment (Optional)
     * @param fileAndImageTakePickerInterface call for Picker Helper class
     * */
    internal fun initPickAPhotoFromGalleryResultLauncher(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
        fileAndImageTakePickerInterface: ImagePickerInterface?
    ) {
        fragmentActivity?.let {
            pickImageFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickAImage(
                        uri = uri,
                        contentResolver = it.contentResolver,
                        fileAndImageTakePickerInterface = fileAndImageTakePickerInterface
                    )
                }
        }
        fragment?.let {
            pickImageFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickAImage(
                        uri = uri,
                        contentResolver = it.requireActivity().contentResolver,
                        fileAndImageTakePickerInterface = fileAndImageTakePickerInterface
                    )
                }
        }
    }

    /**
     * @param uri get a uri with images
     * @param contentResolver content resolver from Activity
     * @param fileAndImageTakePickerInterface call for Picker Helper class
     * */
    private fun handlePickAImage(
        uri: Uri?,
        contentResolver: ContentResolver,
        fileAndImageTakePickerInterface: ImagePickerInterface?
    ) {
        val bitmap: Bitmap?
        try {
            if (uri != null) {
                bitmap = convertUriToBitmap(contentResolver = contentResolver, uri = uri)
                fileAndImageTakePickerInterface?.onBitmap(
                    bitmap = bitmap,
                    uri = uri,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fileAndImageTakePickerInterface?.onBitmap(
                bitmap = null,
                uri = null,
            )
        }
    }

    /**
     * @param fragmentActivity instance for current Activity
     * @param fragment instance for current Fragment
     * */
    internal fun pickMultipleImagesFromGallery(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
    ) {
        fragmentActivity?.let {
            pickMultipleImageFromGalleryResultLauncher?.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        fragment?.let {
            pickMultipleImageFromGalleryResultLauncher?.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }

    /**
     * @param fragmentActivity instance for current Activity
     * @param fragment instance for current Fragment
     * @param maxNumberOfImages max number for select images from picker
     * @param fileAndImageTakePickerInterface call for Picker Helper class
     * */
    internal fun initPickMultiplePhotoFromGalleryResultLauncher(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
        @IntRange(from = 1, to = Long.MAX_VALUE) maxNumberOfImages: Int = 9,
        coroutineScope: CoroutineScope,
        fileAndImageTakePickerInterface: ImagePickerInterface?
    ) {
        fragmentActivity?.let {
            pickMultipleImageFromGalleryResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(maxNumberOfImages)
            ) { uris ->
                coroutineScope.launch(Dispatchers.Default) {
                    handleTheMultipleImagesPicker(
                        uris = uris,
                        contentResolver = it.contentResolver,
                        fileAndImageTakePickerInterface = fileAndImageTakePickerInterface
                    )
                }
            }
        }
        fragment?.let {
            pickMultipleImageFromGalleryResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(maxNumberOfImages)
            ) { uris ->
                coroutineScope.launch(Dispatchers.Default) {
                    handleTheMultipleImagesPicker(
                        uris = uris,
                        contentResolver = it.requireActivity().contentResolver,
                        fileAndImageTakePickerInterface = fileAndImageTakePickerInterface
                    )
                }
            }
        }
    }

    /**
     * @param uris get a list of uri with images
     * @param contentResolver content resolver from Activity
     * @param fileAndImageTakePickerInterface call for Picker Helper class
     * */
    private suspend fun handleTheMultipleImagesPicker(
        uris: List<Uri>?,
        contentResolver: ContentResolver,
        fileAndImageTakePickerInterface: ImagePickerInterface?
    ) = withContext(Dispatchers.Default) {
        try {
            if (!uris.isNullOrEmpty()) {
                val bitmapList = mutableListOf<Bitmap>()
                uris.forEach { uri ->
                    val bitmap =
                        convertUriToBitmap(
                            contentResolver = contentResolver,
                            uri = uri
                        )
                    if (bitmap != null) bitmapList.add(bitmap)
                }
                fileAndImageTakePickerInterface?.onMultipleBitmaps(
                    bitmapList = bitmapList,
                    uriList = uris.toMutableList(),
                )
            } else {
                fileAndImageTakePickerInterface?.onMultipleBitmaps(
                    bitmapList = null,
                    uriList = null,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            fileAndImageTakePickerInterface?.onMultipleBitmaps(
                bitmapList = null,
                uriList = null,
            )
        }
    }

    private fun convertUriToBitmap(
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
}