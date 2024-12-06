package com.nicos.imagepickerandroid.image_picker

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IntRange
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.nicos.imagepickerandroid.utils.image_helper_methods.ImageHelperMethods
import com.nicos.imagepickerandroid.utils.image_helper_methods.ScaleBitmapModel
import com.nicos.imagepickerandroid.utils.permissions.PermissionsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @param fragmentActivity instance for current Activity (Optional), but need one of the two, FragmentActivity/Fragment
 * @param fragment instance for current Fragment (Optional), but need one of the two, FragmentActivity/Fragment
 * @param coroutineScope coroutine scope from Activity or Fragment
 * @param scaleBitmapModelForSingleImage pass ScaleBitmapModel with height and width to resize an image
 * @param scaleBitmapModelForMultipleImages pass ScaleBitmapModel with height and width to resize multiple images
 * @param scaleBitmapModelForCameraImage pass ScaleBitmapModel with height and width to resize the camera image
 * @param enabledBase64ValueForSingleImage convert the image to base64 for a single image
 * @param enabledBase64ValueForMultipleImages convert the image to base64 for multiples images
 * @param enabledBase64ValueForCameraImage convert the image to base64 for a camera image
 * @param imagePickerInterface call back, return the data to the activity/fragment
 * */
data class ImagePicker(
    private var fragmentActivity: FragmentActivity? = null,
    private var fragment: Fragment? = null,
    private var coroutineScope: CoroutineScope,
    var scaleBitmapModelForSingleImage: ScaleBitmapModel? = null,
    var scaleBitmapModelForMultipleImages: ScaleBitmapModel? = null,
    var scaleBitmapModelForCameraImage: ScaleBitmapModel? = null,
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
    private var pickVideoFromGalleryResultLauncher: ActivityResultLauncher<PickVisualMediaRequest>? =
        null
    private var imageHelperMethods = ImageHelperMethods()
    private var permissionsHelper: PermissionsHelper? = null

    init {
        require(fragmentActivity != null || fragment != null) { "pass activity or fragment" }
    }

    /**
     * Method call in listener to open image picker
     * */
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

    /**
     * This method is the initialization for single image from gallery
     * */
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
    ) = coroutineScope.launch(Dispatchers.Main) {
        try {
            if (uri != null) {
                val bitmap = imageHelperMethods.convertUriToBitmap(
                    contentResolver = contentResolver,
                    uri = uri
                )
                if (scaleBitmapModelForSingleImage != null) {
                    imageHelperMethods.scaleBitmap(bitmap, scaleBitmapModelForSingleImage!!)
                        .collect {
                            handleImage(uri = uri, bitmap = bitmap)
                        }
                } else {
                    handleImage(uri = uri, bitmap = bitmap)
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

    /**
     * This method handle the image on UI
     * */
    private fun handleImage(uri: Uri?, bitmap: Bitmap?) = coroutineScope.launch(Dispatchers.Main) {
        if (enabledBase64ValueForSingleImage) {
            imageHelperMethods.convertBitmapToBase64(bitmap)
                .collect { base64AsString ->
                    imagePickerInterface?.onGallerySingleImageWithBase64Value(
                        bitmap = bitmap,
                        uri = uri,
                        base64AsString = base64AsString,
                    )
                }
        } else {
            imagePickerInterface?.onGallerySingleImage(
                bitmap = bitmap,
                uri = uri,
            )
        }
    }

    /**
     * Method call in listener to open image picker
     * */
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
     * @param maxNumberOfImages max number for select images from picker, by default is 9
     * */
    fun initPickMultipleImagesFromGalleryResultLauncher(
        @IntRange(from = 1, to = 9) maxNumberOfImages: Int = 9,
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
                            imageHelperMethods.convertUriToBitmap(
                                contentResolver = contentResolver,
                                uri = uri
                            )
                        if (bitmap != null) bitmapList.add(bitmap)
                    }
                }
                if (scaleBitmapModelForMultipleImages != null) {
                    imageHelperMethods.scaleBitmapList(
                        bitmapList = bitmapList,
                        scaleBitmapModel = scaleBitmapModelForMultipleImages!!
                    ).collect {
                        handleMultipleImages(bitmapList = bitmapList, uris = uris)
                    }
                } else {
                    handleMultipleImages(bitmapList = bitmapList, uris = uris)
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

    /**
     * @param bitmapList list of bitmap
     * @param uris list of uri
     * */
    private fun handleMultipleImages(bitmapList: MutableList<Bitmap>?, uris: List<Uri>) =
        coroutineScope.launch(Dispatchers.Main) {
            if (enabledBase64ValueForMultipleImages) {
                imageHelperMethods.convertListOfBitmapsToListOfBase64(bitmapList)
                    .collect { base64AsStringList ->
                        imagePickerInterface?.onMultipleGalleryImagesWithBase64Value(
                            bitmapList = bitmapList,
                            uriList = uris.toMutableList(),
                            base64AsStringList = base64AsStringList,
                        )
                    }
            } else {
                imagePickerInterface?.onMultipleGalleryImages(
                    bitmapList = bitmapList,
                    uriList = uris.toMutableList(),
                )
            }
        }

    fun takeSinglePhotoWithCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            fragmentActivity?.let {
                if (permissionsHelper?.isPermissionGranted(it) == true)
                    takeAPhotoWithCameraResultLauncher?.launch(this)
                else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            it,
                            Manifest.permission.CAMERA
                        )
                    ) {
                        it.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", it.packageName, null)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    } else {
                        permissionsHelper?.activityResultLauncherPermissionActivity?.launch(Manifest.permission.CAMERA)

                    }
                }
            }
            fragment?.let {
                if (permissionsHelper?.isPermissionGranted(it) == true)
                    takeAPhotoWithCameraResultLauncher?.launch(this)
                else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            it.requireActivity(),
                            Manifest.permission.CAMERA
                        )
                    ) {
                        it.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", it.context?.packageName, null)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    } else {
                        permissionsHelper?.activityResultLauncherPermissionFragment?.launch(Manifest.permission.CAMERA)

                    }
                }
            }
        }
    }

    fun initTakePhotoWithCameraResultLauncher() {
        permissionsHelper = PermissionsHelper(this)

        fragmentActivity?.let {
            permissionsHelper?.initRegisterForRequestPermissionInActivity(fragmentActivity = fragmentActivity)
            takeAPhotoWithCameraResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.apply {
                        handleImageFromCamera(
                            this,
                        )
                    }
                }
            }
        }
        fragment?.let {
            permissionsHelper?.initRegisterForRequestPermissionInFragment(fragment = it)
            takeAPhotoWithCameraResultLauncher = it.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.apply {
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
    ) = coroutineScope.launch(Dispatchers.Main) {
        val bitmap = imageHelperMethods.getExtrasBitmapAccordingWithSDK(intent)
        if (scaleBitmapModelForCameraImage != null) {
            imageHelperMethods.scaleBitmap(bitmap, scaleBitmapModelForCameraImage!!)
                .collect {
                    handleCameraImage(bitmap = bitmap)
                }
        } else {
            handleCameraImage(bitmap = bitmap)
        }

    }

    private fun handleCameraImage(bitmap: Bitmap?) = coroutineScope.launch(Dispatchers.Main) {
        if (enabledBase64ValueForCameraImage) {
            imageHelperMethods.convertBitmapToBase64(bitmap)
                .collect { base64AsString ->
                    imagePickerInterface?.onCameraImageWithBase64Value(
                        bitmap = bitmap,
                        base64AsString = base64AsString,
                    )
                }
        } else {
            imagePickerInterface?.onCameraImage(
                bitmap = bitmap,
            )
        }
    }

    /**
     * Method call in listener to open the picker
     * */
    fun pickSingleVideoFromGallery() {
        fragmentActivity?.let {
            pickVideoFromGalleryResultLauncher?.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.VideoOnly
                )
            )
        }
        fragment?.let {
            pickVideoFromGalleryResultLauncher?.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.VideoOnly
                )
            )
        }
    }

    /**
     * This method is the initialization for single video from gallery
     * */
    fun initPickSingleVideoFromGalleryResultLauncher() {
        fragmentActivity?.let {
            pickVideoFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickSingleVideo(
                        uri = uri,
                    )
                }
        }
        fragment?.let {
            pickVideoFromGalleryResultLauncher =
                it.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    handlePickSingleVideo(
                        uri = uri,
                    )
                }
        }
    }

    /**
     * @param uri get a uri with images
     * */
    private fun handlePickSingleVideo(
        uri: Uri?,
    ) = coroutineScope.launch(Dispatchers.Main) {
        try {
            if (uri != null) {
                imagePickerInterface?.onGallerySingleVideo(
                    uri = uri,
                )
            } else {
                imagePickerInterface?.onGallerySingleVideo(
                    uri = null,
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            imagePickerInterface?.onGallerySingleVideo(
                uri = null,
            )
        }
    }
}