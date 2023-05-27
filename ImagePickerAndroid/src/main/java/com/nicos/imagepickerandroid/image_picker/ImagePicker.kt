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

object ImagePicker : PermissionsHelper() {

    private var pickImageFromGalleryResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? =
        null
    private var pickMultipleImageFromGalleryResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? =
        null
    private var takeAPhotoWithCameraResultLauncher: ActivityResultLauncher<Intent>? = null

    private var imageHelperMethod = ImageHelperMethod()

    /**
     * @param fragmentActivity instance for current Activity (Optional)
     * @param fragment instance for current Fragment (Optional)
     * */
    fun pickSingleImageFromGallery(
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
    fun initPickSingleImageFromGalleryResultLauncher(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
        coroutineScope: CoroutineScope,
        enabledBase64: Boolean = false,
        imagePickerInterface: ImagePickerInterface?
    ) {
        fragmentActivity?.let {
            pickImageFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickSingleImage(
                        uri = uri,
                        contentResolver = it.contentResolver,
                        coroutineScope = coroutineScope,
                        enabledBase64 = enabledBase64,
                        imagePickerInterface = imagePickerInterface
                    )
                }
        }
        fragment?.let {
            pickImageFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickSingleImage(
                        uri = uri,
                        contentResolver = it.requireActivity().contentResolver,
                        coroutineScope = coroutineScope,
                        enabledBase64 = enabledBase64,
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
    private fun handlePickSingleImage(
        uri: Uri?,
        contentResolver: ContentResolver,
        coroutineScope: CoroutineScope,
        enabledBase64: Boolean,
        imagePickerInterface: ImagePickerInterface?
    ) {
        try {
            if (uri != null) {
                val bitmap = convertUriToBitmap(contentResolver = contentResolver, uri = uri)
                if (enabledBase64) {
                    coroutineScope.launch(Dispatchers.Default) {
                        imageHelperMethod.convertBitmapToBase64(bitmap)
                            .collect { base64AsString ->
                                coroutineScope.launch(Dispatchers.Main) {
                                    imagePickerInterface?.onGalleryImage(
                                        bitmap = bitmap,
                                        uri = uri,
                                        base64AsString = base64AsString,
                                    )
                                }
                            }
                    }
                } else {
                    imagePickerInterface?.onGalleryImage(
                        bitmap = bitmap,
                        uri = uri,
                        base64AsString = null,
                    )
                }
            } else {
                imagePickerInterface?.onGalleryImage(
                    bitmap = null,
                    uri = null,
                    base64AsString = null,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imagePickerInterface?.onGalleryImage(
                bitmap = null,
                uri = null,
                base64AsString = null,
            )
        }
    }

    /**
     * @param fragmentActivity instance for current Activity
     * @param fragment instance for current Fragment
     * */
    fun pickMultipleImagesFromGallery(
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
    fun initPickMultipleImagesFromGalleryResultLauncher(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
        @IntRange(from = 1, to = Long.MAX_VALUE) maxNumberOfImages: Int = 9,
        coroutineScope: CoroutineScope,
        enabledBase64: Boolean = false,
        imagePickerInterface: ImagePickerInterface?
    ) {
        fragmentActivity?.let {
            pickMultipleImageFromGalleryResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(maxNumberOfImages)
            ) { uris ->
                handleTheMultipleImagesPicker(
                    uris = uris,
                    contentResolver = it.contentResolver,
                    coroutineScope = coroutineScope,
                    enabledBase64 = enabledBase64,
                    imagePickerInterface = imagePickerInterface,
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
                    coroutineScope = coroutineScope,
                    enabledBase64 = enabledBase64,
                    imagePickerInterface = imagePickerInterface,
                )
            }
        }
    }

    /**
     * @param uris get a list of uri with images
     * @param contentResolver content resolver from Activity
     * @param imagePickerInterface call for Picker Helper class
     * */
    private fun handleTheMultipleImagesPicker(
        uris: List<Uri>?,
        contentResolver: ContentResolver,
        coroutineScope: CoroutineScope,
        enabledBase64: Boolean = false,
        imagePickerInterface: ImagePickerInterface?
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
                if (enabledBase64) {
                    coroutineScope.launch(Dispatchers.Default) {
                        imageHelperMethod.convertListOfBitmapsToListOfBase64(bitmapList)
                            .collect { base64AsStringList ->
                                coroutineScope.launch(Dispatchers.Main) {
                                    imagePickerInterface?.onMultipleGalleryImages(
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
                        base64AsStringList = null,
                    )
                }
            } else {
                imagePickerInterface?.onMultipleGalleryImages(
                    bitmapList = null,
                    uriList = null,
                    base64AsStringList = null,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imagePickerInterface?.onMultipleGalleryImages(
                bitmapList = null,
                uriList = null,
                base64AsStringList = null,
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
    fun takeSinglePhotoWithCamera(
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
    fun initTakePhotoWithCameraResultLauncher(
        fragmentActivity: FragmentActivity? = null,
        fragment: Fragment? = null,
        coroutineScope: CoroutineScope,
        enabledBase64: Boolean = false,
        imagePickerInterface: ImagePickerInterface?
    ) {
        fragmentActivity?.let {
            initRegisterForRequestPermissionInActivity(fragmentActivity = fragmentActivity)
            takeAPhotoWithCameraResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result?.data?.apply {
                        handleImageFromCamera(
                            coroutineScope,
                            enabledBase64,
                            this,
                            imagePickerInterface
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
                        handleImageFromCamera(
                            coroutineScope,
                            enabledBase64,
                            this,
                            imagePickerInterface
                        )
                    }
                }
            }
        }
    }

    private fun handleImageFromCamera(
        coroutineScope: CoroutineScope,
        enabledBase64: Boolean = false,
        intent: Intent,
        imagePickerInterface: ImagePickerInterface?,
    ) {
        val bitmap = getExtrasBitmapAccordingWithSDK(intent)
        if (enabledBase64) {
            coroutineScope.launch(Dispatchers.Default) {
                imageHelperMethod.convertBitmapToBase64(bitmap)
                    .collect { base64AsString ->
                        coroutineScope.launch(Dispatchers.Main) {
                            imagePickerInterface?.onCameraImage(
                                bitmap = bitmap,
                                base64AsString = base64AsString,
                            )
                        }
                    }
            }
        } else {
            imagePickerInterface?.onCameraImage(
                bitmap = bitmap,
                base64AsString = null,
            )
        }
    }

    private fun getExtrasBitmapAccordingWithSDK(intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.extras?.getParcelable(
            "data",
            Bitmap::class.java
        ) else intent.extras?.get("data") as? Bitmap
}