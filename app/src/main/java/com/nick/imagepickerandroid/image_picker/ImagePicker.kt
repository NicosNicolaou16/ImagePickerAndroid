package com.nick.imagepickerandroid.image_picker

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
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
import com.nick.imagepickerandroid.utils.permissions.PermissionsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImagePicker : PermissionsHelper() {

    private var pickImageFromGalleryResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? =
        null
    private var pickMultipleImageFromGalleryResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? =
        null
    private var takeAPhotoWithCameraResultLauncher: ActivityResultLauncher<Intent>? = null

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
     * @param imagePickerInterface call for Picker Helper class
     * */
    internal fun initPickAPhotoFromGalleryResultLauncher(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
        imagePickerInterface: ImagePickerInterface?
    ) {
        fragmentActivity?.let {
            pickImageFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickAImage(
                        uri = uri,
                        contentResolver = it.contentResolver,
                        imagePickerInterface = imagePickerInterface
                    )
                }
        }
        fragment?.let {
            pickImageFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickAImage(
                        uri = uri,
                        contentResolver = it.requireActivity().contentResolver,
                        imagePickerInterface = imagePickerInterface
                    )
                }
        }
    }

    /**
     * @param uri get a uri with images
     * @param contentResolver content resolver from Activity
     * @param imagePickerInterface call for Picker Helper class
     * */
    private fun handlePickAImage(
        uri: Uri?,
        contentResolver: ContentResolver,
        imagePickerInterface: ImagePickerInterface?
    ) {
        val bitmap: Bitmap?
        try {
            if (uri != null) {
                bitmap = convertUriToBitmap(contentResolver = contentResolver, uri = uri)
                imagePickerInterface?.onBitmapGallery(
                    bitmap = bitmap,
                    uri = uri,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imagePickerInterface?.onBitmapGallery(
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
     * @param imagePickerInterface call for Picker Helper class
     * */
    internal fun initPickMultiplePhotoFromGalleryResultLauncher(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
        @IntRange(from = 1, to = Long.MAX_VALUE) maxNumberOfImages: Int = 9,
        coroutineScope: CoroutineScope,
        imagePickerInterface: ImagePickerInterface?
    ) {
        fragmentActivity?.let {
            pickMultipleImageFromGalleryResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(maxNumberOfImages)
            ) { uris ->
                coroutineScope.launch(Dispatchers.Default) {
                    handleTheMultipleImagesPicker(
                        uris = uris,
                        contentResolver = it.contentResolver,
                        imagePickerInterface = imagePickerInterface
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
                        imagePickerInterface = imagePickerInterface
                    )
                }
            }
        }
    }

    /**
     * @param uris get a list of uri with images
     * @param contentResolver content resolver from Activity
     * @param imagePickerInterface call for Picker Helper class
     * */
    private suspend fun handleTheMultipleImagesPicker(
        uris: List<Uri>?,
        contentResolver: ContentResolver,
        imagePickerInterface: ImagePickerInterface?
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
                imagePickerInterface?.onMultipleBitmapsGallery(
                    bitmapList = bitmapList,
                    uriList = uris.toMutableList(),
                )
            } else {
                imagePickerInterface?.onMultipleBitmapsGallery(
                    bitmapList = null,
                    uriList = null,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imagePickerInterface?.onMultipleBitmapsGallery(
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

    /**
     * @param fragmentActivity instance for current Activity
     * @param fragment instance for current Fragment
     * @param permissionTag separate the Camera Permission from other Permission if it has
     * */
    internal fun takeAPhotoWithCamera(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
    ) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            fragmentActivity?.let {
                if (isPermissionGranted(it))
                    takeAPhotoWithCameraResultLauncher?.launch(this)
                else {
                    activityResultLauncherPermissionActivity?.launch(Manifest.permission.CAMERA)
                }
            }
            fragment?.let {
                if (isPermissionGranted(it))
                    takeAPhotoWithCameraResultLauncher?.launch(this)
                else {
                    activityResultLauncherPermissionFragment?.launch(Manifest.permission.CAMERA)
                }
            }
        }
    }

    /**
     * @param fragmentActivity instance for current Activity
     * @param fragment instance for current Fragment
     * @param imagePickerInterface call for Picker Helper class
     * */
    internal fun initTakeAPhotoWithCameraResultLauncher(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
        imagePickerInterface: ImagePickerInterface?
    ) {
        fragmentActivity?.let {
            initRegisterForRequestPermissionInActivity(fragmentActivity = fragmentActivity)
            takeAPhotoWithCameraResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result?.data?.apply {
                        val bitmap = getExtrasBitmapAccordingWithSDK(this)
                        imagePickerInterface?.onBitmapCamera(
                            bitmap = bitmap,
                            uri = null,
                        )
                    }
                }
            }
        }
        fragment?.let {
            initRegisterForRequestPermissionInFragment(fragment = it)
            takeAPhotoWithCameraResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result?.data?.apply {
                        val bitmap = getExtrasBitmapAccordingWithSDK(this)
                        imagePickerInterface?.onBitmapCamera(
                            bitmap = bitmap,
                            uri = null,
                        )
                    }
                }
            }
        }
    }

    private fun getExtrasBitmapAccordingWithSDK(intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.extras?.getParcelable(
            "data",
            Bitmap::class.java
        ) else intent.extras?.get("data") as? Bitmap
}