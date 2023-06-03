package com.nicos.imagepickerandroid.image_picker

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
import com.nicos.imagepickerandroid.utils.image_helper_methods.ImageHelperMethod
import com.nicos.imagepickerandroid.utils.permissions.PermissionsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @param fragmentActivity instance for current Activity (Optional), but need one of the two, FragmentActivity/Fragment
 * @param fragment instance for current Fragment (Optional), but need one of the two, FragmentActivity/Fragment
 * @param coroutineScope coroutine scope from Activity or Fragment
 * @param enabledBase64ValueForSingleImage convert the image to base64 for a single image
 * @param enabledBase64ValueForMultipleImages convert the image to base64 for multiples images
 * @param enabledBase64ValueForCameraImage convert the image to base64 for a camera image
 * @param imagePickerInterface call back, return the data to the activity/fragment
 * */
data class ImagePicker(
    private var fragmentActivity: FragmentActivity? = null,
    private var fragment: Fragment? = null,
    private var coroutineScope: CoroutineScope,
    private var enabledBase64ValueForSingleImage: Boolean = false,
    private var enabledBase64ValueForMultipleImages: Boolean = false,
    private var enabledBase64ValueForCameraImage: Boolean = false,
    var imagePickerInterface: ImagePickerInterface?
) {
    private var pickImageFromGalleryResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? =
        null
    private var pickMultipleImageFromGalleryResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? =
        null
    private var takeAPhotoWithCameraResultLauncher: ActivityResultLauncher<Intent>? = null

    private var imageHelperMethod = ImageHelperMethod()
    private val permissionsHelper = PermissionsHelper(this)

    init {
        require(fragmentActivity != null || fragment != null) { "pass activity or fragment" }
    }

    fun pickSingleImageFromGallery() {
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

    fun initPickSingleImageFromGalleryResultLauncher() {
        fragmentActivity?.let {
            pickImageFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickSingleImage(
                        uri = uri,
                        contentResolver = it.contentResolver,
                    )
                }
        }
        fragment?.let {
            pickImageFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickSingleImage(
                        uri = uri,
                        contentResolver = it.requireActivity().contentResolver,
                    )
                }
        }
    }

    /**
     * @param uri get a uri with images
     * @param contentResolver content resolver from Activity
     * */
    private fun handlePickSingleImage(
        uri: Uri?,
        contentResolver: ContentResolver,
    ) {
        try {
            if (uri != null) {
                val bitmap = convertUriToBitmap(contentResolver = contentResolver, uri = uri)
                if (enabledBase64ValueForSingleImage) {
                    coroutineScope.launch(Dispatchers.Default) {
                        imageHelperMethod.convertBitmapToBase64(bitmap)
                            .collect { base64AsString ->
                                coroutineScope.launch(Dispatchers.Main) {
                                    imagePickerInterface?.onGallerySingleImageWithBase64Value(
                                        bitmap = bitmap,
                                        uri = uri,
                                        base64AsString = base64AsString,
                                    )
                                }
                            }
                    }
                } else {
                    imagePickerInterface?.onGallerySingleImage(
                        bitmap = bitmap,
                        uri = uri,
                    )
                }
            } else {
                imagePickerInterface?.onGallerySingleImage(
                    bitmap = null,
                    uri = null,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imagePickerInterface?.onGallerySingleImage(
                bitmap = null,
                uri = null,
            )
        }
    }

    fun pickMultipleImagesFromGallery() {
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
     * @param maxNumberOfImages max number for select images from picker
     * */
    fun initPickMultipleImagesFromGalleryResultLauncher(
        @IntRange(from = 1, to = Long.MAX_VALUE) maxNumberOfImages: Int = 9,
    ) {
        fragmentActivity?.let {
            pickMultipleImageFromGalleryResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(maxNumberOfImages)
            ) { uris ->
                handleTheMultipleImagesPicker(
                    uris = uris,
                    contentResolver = it.contentResolver,
                )
            }
        }
        fragment?.let {
            pickMultipleImageFromGalleryResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(maxNumberOfImages)
            ) { uris ->
                handleTheMultipleImagesPicker(
                    uris = uris,
                    contentResolver = it.requireActivity().contentResolver,
                )
            }
        }
    }

    /**
     * @param uris get a list of uri with images
     * @param contentResolver content resolver from Activity
     * */
    private fun handleTheMultipleImagesPicker(
        uris: List<Uri>?,
        contentResolver: ContentResolver,
    ) = coroutineScope.launch(Dispatchers.Main) {
        try {
            if (!uris.isNullOrEmpty()) {
                val bitmapList = mutableListOf<Bitmap>()
                withContext(Dispatchers.Default) {
                    uris.forEach { uri ->
                        val bitmap =
                            convertUriToBitmap(
                                contentResolver = contentResolver,
                                uri = uri
                            )
                        if (bitmap != null) bitmapList.add(bitmap)
                    }
                }
                if (enabledBase64ValueForMultipleImages) {
                    coroutineScope.launch(Dispatchers.Default) {
                        imageHelperMethod.convertListOfBitmapsToListOfBase64(bitmapList)
                            .collect { base64AsStringList ->
                                coroutineScope.launch(Dispatchers.Main) {
                                    imagePickerInterface?.onMultipleGalleryImagesWithBase64Value(
                                        bitmapList = bitmapList,
                                        uriList = uris.toMutableList(),
                                        base64AsStringList = base64AsStringList,
                                    )
                                }
                            }
                    }
                } else {
                    imagePickerInterface?.onMultipleGalleryImages(
                        bitmapList = bitmapList,
                        uriList = uris.toMutableList(),
                    )
                }
            } else {
                imagePickerInterface?.onMultipleGalleryImages(
                    bitmapList = null,
                    uriList = null,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imagePickerInterface?.onMultipleGalleryImages(
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

    fun takeSinglePhotoWithCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            fragmentActivity?.let {
                if (permissionsHelper.isPermissionGranted(it))
                    takeAPhotoWithCameraResultLauncher?.launch(this)
                else {
                    permissionsHelper.activityResultLauncherPermissionActivity?.launch(Manifest.permission.CAMERA)
                }
            }
            fragment?.let {
                if (permissionsHelper.isPermissionGranted(it))
                    takeAPhotoWithCameraResultLauncher?.launch(this)
                else {
                    permissionsHelper.activityResultLauncherPermissionFragment?.launch(Manifest.permission.CAMERA)
                }
            }
        }
    }

    fun initTakePhotoWithCameraResultLauncher() {
        fragmentActivity?.let {
            permissionsHelper.initRegisterForRequestPermissionInActivity(fragmentActivity = fragmentActivity)
            takeAPhotoWithCameraResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result?.data?.apply {
                        handleImageFromCamera(
                            this,
                        )
                    }
                }
            }
        }
        fragment?.let {
            permissionsHelper.initRegisterForRequestPermissionInFragment(fragment = it)
            takeAPhotoWithCameraResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result?.data?.apply {
                        handleImageFromCamera(
                            this,
                        )
                    }
                }
            }
        }
    }

    private fun handleImageFromCamera(
        intent: Intent,
    ) {
        val bitmap = getExtrasBitmapAccordingWithSDK(intent)
        if (enabledBase64ValueForCameraImage) {
            coroutineScope.launch(Dispatchers.Default) {
                imageHelperMethod.convertBitmapToBase64(bitmap)
                    .collect { base64AsString ->
                        coroutineScope.launch(Dispatchers.Main) {
                            imagePickerInterface?.onCameraImageWithBase64Value(
                                bitmap = bitmap,
                                base64AsString = base64AsString,
                            )
                        }
                    }
            }
        } else {
            imagePickerInterface?.onCameraImage(
                bitmap = bitmap,
            )
        }
    }

    private fun getExtrasBitmapAccordingWithSDK(intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.extras?.getParcelable(
            "data",
            Bitmap::class.java
        ) else intent.extras?.get("data") as? Bitmap
}