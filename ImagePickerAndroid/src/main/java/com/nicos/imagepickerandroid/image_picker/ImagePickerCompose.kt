package com.nicos.imagepickerandroid.image_picker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.nick.imagepickerandroid.BuildConfig
import com.nicos.imagepickerandroid.utils.constants.Constants.TAG
import com.nicos.imagepickerandroid.utils.constants.Constants.imagePickerNotAvailableLogs
import com.nicos.imagepickerandroid.utils.enums.TakeImageType
import com.nicos.imagepickerandroid.utils.extensions.getUriWithFileProvider
import com.nicos.imagepickerandroid.utils.image_helper_methods.ImageHelperMethods
import com.nicos.imagepickerandroid.utils.image_helper_methods.ScaleBitmapModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @param permissionLauncherCameraImage launcher for camera permission
 * @param permissionCameraImageWithBase64Launcher launcher for camera permission with base64 value
 * */
private var permissionLauncherCameraImage: ManagedActivityResultLauncher<String, Boolean>? = null
private var permissionCameraImageWithBase64Launcher: ManagedActivityResultLauncher<String, Boolean>? =
    null

/**
 * @param imageHelperMethods instance for image helper methods
 * */
private var imageHelperMethods = ImageHelperMethods()

/**
 * @param pickSingleImage launcher for single image from gallery
 * @param pickSingleImageWithBase64Value launcher for single image from gallery with base64 value
 * @param pickMultipleImages launcher for multiple images from gallery
 * @param pickMultipleImagesWithBase64Values launcher for multiple images from gallery with base64 values
 * @param takeCameraImage launcher for single image from camera
 * @param takeCameraImagePreview launcher for single image preview from camera
 * @param takeCameraImageWithBase64Value launcher for single image from camera with base64 value
 * @param takeCameraImagePreviewWithBase64Value launcher for single image preview from camera with base64 value
 * @param pickVideo launcher for single video from gallery
 * */
private var pickSingleImage: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? = null
private var pickSingleImageWithBase64Value: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? =
    null
private var pickMultipleImages: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>? =
    null
private var pickMultipleImagesWithBase64Values: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>? =
    null
private var takeCameraImagePreview: ManagedActivityResultLauncher<Void?, Bitmap?>? = null
private var takeCameraImage: ManagedActivityResultLauncher<Uri, Boolean>? = null
private var takeCameraImagePreviewWithBase64Value: ManagedActivityResultLauncher<Void?, Bitmap?>? =
    null
private var takeCameraImageWithBase64Value: ManagedActivityResultLauncher<Uri, Boolean>? = null
private var pickVideo: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? = null

/**
 * @param photoUri pass Uri with the image
 * @param photoUriWithBase64 pass Uri with the image
 * */
private var photoUri by mutableStateOf<Uri?>(null)
private var photoUriWithBase64 by mutableStateOf<Uri?>(null)

/**
 * Callback for the single image to view
 * @param scaleBitmapModel pass ScaleBitmapModel with height and width to resize an image
 * @param listener return the value to view
 * */
@Composable
fun PickSingleImage(
    scaleBitmapModel: ScaleBitmapModel?,
    listener: (Bitmap?, Uri?) -> Unit
) {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    pickSingleImage =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            var bitmap: Bitmap? = null
            if (uri != null) {
                bitmap = imageHelperMethods.convertUriToBitmap(
                    contentResolver = context.contentResolver,
                    uri = uri
                )
            }
            if (scaleBitmapModel != null) {
                composableScope.launch(Dispatchers.Default) {
                    imageHelperMethods.scaleBitmap(
                        bitmap = bitmap,
                        scaleBitmapModel = scaleBitmapModel
                    ).collect { scaledBitmap ->
                        composableScope.launch(Dispatchers.Main) {
                            listener(scaledBitmap, uri)
                        }
                    }
                }
            } else {
                listener(bitmap, uri)
            }
        }
}

/**
 * This method is calling from listener to pick single image
 * @param context pass context
 * @param onImagePickerNotAvailable callback for image picker not available
 * */
fun pickSingleImage(
    context: Context,
    onImagePickerNotAvailable: (() -> Unit)? = null
) {
    if (isPickerAvailable(context = context)) {
        pickSingleImage?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    } else {
        imagePickerNotAvailableLogs()
        onImagePickerNotAvailable?.invoke()
    }
}

/**
 * Callback for single image to view with base64 value
 * @param scaleBitmapModel pass ScaleBitmapModel with height and width to resize an image
 * @param listener return the image to view
 * */
@Composable
fun PickSingleImageWithBase64Value(
    scaleBitmapModel: ScaleBitmapModel?,
    listener: (Bitmap?, Uri?, String?) -> Unit
) {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    pickSingleImageWithBase64Value =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            val bitmap: Bitmap?
            if (uri != null) {
                bitmap = imageHelperMethods.convertUriToBitmap(
                    contentResolver = context.contentResolver,
                    uri = uri
                )
                if (scaleBitmapModel != null) {
                    composableScope.launch(Dispatchers.Default) {
                        imageHelperMethods.scaleBitmap(
                            bitmap = bitmap,
                            scaleBitmapModel = scaleBitmapModel
                        ).collect { scaledBitmap ->
                            imageHelperMethods.convertBitmapToBase64(bitmap = bitmap)
                                .collect { base64 ->
                                    composableScope.launch(Dispatchers.Main) {
                                        listener(scaledBitmap, uri, base64)
                                    }
                                }
                        }
                    }
                } else {
                    composableScope.launch(Dispatchers.Default) {
                        imageHelperMethods.convertBitmapToBase64(bitmap = bitmap)
                            .collect { base64 ->
                                composableScope.launch(Dispatchers.Main) {
                                    listener(bitmap, uri, base64)
                                }
                            }
                    }
                }
            }
        }
}

/**
 * This method is calling from listener to pick single image with base64 value
 * @param context pass context
 * @param onImagePickerNotAvailable callback for image picker not available
 * */
fun pickSingleImageWithBase64Value(
    context: Context,
    onImagePickerNotAvailable: (() -> Unit)? = null
) {
    if (isPickerAvailable(context = context)) {
        pickSingleImageWithBase64Value?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    } else {
        imagePickerNotAvailableLogs()
        onImagePickerNotAvailable?.invoke()
    }
}

/**
 * Callback for the multiple images to list view
 * @param scaleBitmapModel pass ScaleBitmapModel with height and width to resize multiple images
 * @param listener return the images to list view
 * */
@Composable
fun PickMultipleImages(
    scaleBitmapModel: ScaleBitmapModel?,
    @IntRange(from = 1, to = 9) maxNumberOfImages: Int = 9,
    listener: (MutableList<Bitmap>?, MutableList<Uri>?) -> Unit
) {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    pickMultipleImages =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(
                maxItems = maxNumberOfImages
            )
        ) { uriList ->
            composableScope.launch(Dispatchers.Default) {
                val bitmapList = mutableListOf<Bitmap>()
                if (uriList.isNotEmpty()) {
                    uriList.forEach { uri ->
                        val bitmap = imageHelperMethods.convertUriToBitmap(
                            contentResolver = context.contentResolver,
                            uri = uri
                        )
                        if (bitmap != null) bitmapList.add(bitmap)
                    }
                }
                if (scaleBitmapModel != null) {
                    imageHelperMethods.scaleBitmapList(
                        bitmapList = bitmapList,
                        scaleBitmapModel = scaleBitmapModel
                    ).collect { scaledBitmapList ->
                        composableScope.launch(Dispatchers.Main) {
                            listener(scaledBitmapList, uriList.toMutableList())
                        }
                    }
                } else {
                    composableScope.launch(Dispatchers.Main) {
                        listener(bitmapList, uriList.toMutableList())
                    }
                }
            }
        }
}

/**
 * This method is calling from listener to pick multiple images
 * @param context pass context
 * @param onImagePickerNotAvailable callback for image picker not available
 * */
fun pickMultipleImages(
    context: Context,
    onImagePickerNotAvailable: (() -> Unit)? = null
) {
    if (isPickerAvailable(context = context)) {
        pickMultipleImages?.launch(input = PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    } else {
        imagePickerNotAvailableLogs()
        onImagePickerNotAvailable?.invoke()
    }
}

/**
 * Callback for the multiple images to list view with base64 values
 * @param scaleBitmapModel pass ScaleBitmapModel with height and width to resize multiple images
 * @param maxNumberOfImages max number for select images from picker, by default is 9
 * @param listener return the images to list view, list of uri and list of base64
 * */
@Composable
fun PickMultipleImagesWithBase64Values(
    scaleBitmapModel: ScaleBitmapModel?,
    @IntRange(from = 1, to = 9) maxNumberOfImages: Int = 9,
    listener: (MutableList<Bitmap>?, MutableList<Uri>?, MutableList<String>?) -> Unit
) {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    pickMultipleImagesWithBase64Values =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(
                maxItems = maxNumberOfImages
            )
        ) { uriList ->
            composableScope.launch(Dispatchers.Default) {
                val bitmapList = mutableListOf<Bitmap>()
                if (uriList.isNotEmpty()) {
                    uriList.forEach { uri ->
                        val bitmap = imageHelperMethods.convertUriToBitmap(
                            contentResolver = context.contentResolver,
                            uri = uri
                        )
                        if (bitmap != null) bitmapList.add(bitmap)
                    }
                }
                if (scaleBitmapModel != null) {
                    imageHelperMethods.scaleBitmapList(
                        bitmapList = bitmapList,
                        scaleBitmapModel = scaleBitmapModel
                    ).collect { scaledBitmapList ->
                        imageHelperMethods.convertListOfBitmapsToListOfBase64(bitmapList = scaledBitmapList)
                            .collect { base64List ->
                                composableScope.launch(Dispatchers.Main) {
                                    listener(
                                        scaledBitmapList,
                                        uriList.toMutableList(),
                                        base64List
                                    )
                                }
                            }
                    }
                } else {
                    imageHelperMethods.convertListOfBitmapsToListOfBase64(bitmapList = bitmapList)
                        .collect { base64List ->
                            composableScope.launch(Dispatchers.Main) {
                                listener(bitmapList, uriList.toMutableList(), base64List)
                            }
                        }
                }
            }
        }
}

/**
 * This method is calling from listener to pick multiple images with base64 values
 * @param context pass context
 * @param onImagePickerNotAvailable callback for image picker not available
 * */
fun pickMultipleImagesWithBase64Values(
    context: Context,
    onImagePickerNotAvailable: (() -> Unit)? = null
) {
    if (isPickerAvailable(context = context)) {
        pickMultipleImagesWithBase64Values?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    } else {
        imagePickerNotAvailableLogs()
        onImagePickerNotAvailable?.invoke()
    }
}

/**
 * Callback for the single image to view from camera
 * @param scaleBitmapModel pass ScaleBitmapModel with height and width to resize an image
 * @param takeImageType this variable is optional, pass TakeImageType.TAKE_IMAGE if you want to take a picture with camera and TakeImageType.TAKE_IMAGE_PREVIEW to take picture a preview, by default is TakeImageType.TAKE_IMAGE
 * @param listener return the image view and uri
 * */
@Composable
fun TakeSingleCameraImage(
    scaleBitmapModel: ScaleBitmapModel?,
    takeImageType: TakeImageType = TakeImageType.TAKE_IMAGE,
    listener: (Bitmap?, Uri?) -> Unit
) {
    val context = LocalContext.current
    CameraPermission(takeImageType = takeImageType)
    val composableScope = rememberCoroutineScope()
    if (takeImageType == TakeImageType.TAKE_IMAGE) {
        takeCameraImage =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    if (photoUri != null) {
                        val bitmap =
                            imageHelperMethods.convertUriToBitmap(context.contentResolver, photoUri)
                        if (scaleBitmapModel != null) {
                            composableScope.launch(Dispatchers.Default) {
                                imageHelperMethods.scaleBitmap(
                                    bitmap = bitmap,
                                    scaleBitmapModel = scaleBitmapModel
                                ).collect { scaledBitmap ->
                                    composableScope.launch(Dispatchers.Main) {
                                        listener(scaledBitmap, photoUri)
                                    }
                                }
                            }
                        } else {
                            listener(bitmap, photoUri)
                        }
                    }
                }
            }
    } else {
        takeCameraImagePreview =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                if (bitmap != null) {
                    val uri = imageHelperMethods.getUriFromBitmap(bitmap)
                    if (scaleBitmapModel != null) {
                        composableScope.launch(Dispatchers.Default) {
                            imageHelperMethods.scaleBitmap(
                                bitmap = bitmap,
                                scaleBitmapModel = scaleBitmapModel
                            ).collect { scaledBitmap ->
                                composableScope.launch(Dispatchers.Main) {
                                    listener(scaledBitmap, uri)
                                }
                            }
                        }
                    } else {
                        listener(bitmap, uri)
                    }
                }
            }
    }
}

/**
 * @param takeImageType pass TakeImageType.TAKE_IMAGE if you want to take a picture with camera and TakeImageType.TAKE_IMAGE_PREVIEW to take picture a preview
 * */
@Composable
private fun CameraPermission(takeImageType: TakeImageType) {
    val context = LocalContext.current
    if (takeImageType == TakeImageType.TAKE_IMAGE) {
        takeCameraImage =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (!success) {
                    photoUri = null
                }
            }
    }

    permissionLauncherCameraImage = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (takeImageType == TakeImageType.TAKE_IMAGE) {
                val photoFile = imageHelperMethods.createImageFile(context)
                val uri = photoFile.getUriWithFileProvider(context)
                photoUri = uri
                takeCameraImage?.launch(uri)
            } else {
                takeCameraImagePreview?.launch(null)
            }
        }
    }
}


/**
 * This method is calling from listener to pick single image from camera
 * @param context pass context
 * @param onPermanentCameraPermissionDeniedCallBack callback for permanent camera permission denied
 * */
fun takeSingleCameraImage(
    context: Context,
    onPermanentCameraPermissionDeniedCallBack: (() -> Unit)? = null
) {
    if (shouldShowRequestPermissionRationale(
            context as Activity,
            Manifest.permission.CAMERA
        )
    ) {
        if (onPermanentCameraPermissionDeniedCallBack == null) {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            onPermanentCameraPermissionDeniedCallBack()
        }
    } else {
        permissionLauncherCameraImage?.launch(Manifest.permission.CAMERA)
    }
}

/**
 * Callback for the single image to view from camera with base64 value
 * @param scaleBitmapModel pass ScaleBitmapModel with height and width to resize an image
 * @param takeImageType this variable is optional, pass TakeImageType.TAKE_IMAGE if you want to take a picture with camera and TakeImageType.TAKE_IMAGE_PREVIEW to take picture a preview, by default is TakeImageType.TAKE_IMAGE
 * @param listener return the images to view and base64 value
 * */
@Composable
fun TakeSingleCameraImageWithBase64Value(
    scaleBitmapModel: ScaleBitmapModel?,
    takeImageType: TakeImageType = TakeImageType.TAKE_IMAGE,
    listener: (Bitmap?, String?) -> Unit
) {
    val composableScope = rememberCoroutineScope()
    val context = LocalContext.current
    CameraPermissionForBase64(takeImageType = takeImageType)
    if (takeImageType == TakeImageType.TAKE_IMAGE) {
        takeCameraImageWithBase64Value =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    if (photoUriWithBase64 != null) {
                        if (scaleBitmapModel != null) {
                            composableScope.launch(Dispatchers.Default) {
                                val bitmap = imageHelperMethods.convertUriToBitmap(
                                    contentResolver = context.contentResolver,
                                    uri = photoUriWithBase64
                                )
                                imageHelperMethods.scaleBitmap(
                                    bitmap = bitmap,
                                    scaleBitmapModel = scaleBitmapModel
                                ).collect { scaledBitmap ->
                                    imageHelperMethods.convertBitmapToBase64(bitmap = bitmap)
                                        .collect { base64 ->
                                            composableScope.launch(Dispatchers.Main) {
                                                listener(scaledBitmap, base64)
                                            }
                                        }
                                }
                            }
                        } else {
                            composableScope.launch(Dispatchers.Default) {
                                val bitmap = imageHelperMethods.convertUriToBitmap(
                                    contentResolver = context.contentResolver,
                                    uri = photoUriWithBase64
                                )
                                imageHelperMethods.convertBitmapToBase64(bitmap = bitmap)
                                    .collect { base64 ->
                                        composableScope.launch(Dispatchers.Main) {
                                            listener(bitmap, base64)
                                        }
                                    }
                            }
                        }
                    }
                }
            }
    } else {
        takeCameraImagePreviewWithBase64Value =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) { bitmap ->
                if (bitmap != null) {
                    if (scaleBitmapModel != null) {
                        composableScope.launch(Dispatchers.Default) {
                            imageHelperMethods.scaleBitmap(
                                bitmap = bitmap,
                                scaleBitmapModel = scaleBitmapModel
                            ).collect { scaledBitmap ->
                                imageHelperMethods.convertBitmapToBase64(bitmap = bitmap)
                                    .collect { base64 ->
                                        composableScope.launch(Dispatchers.Main) {
                                            listener(scaledBitmap, base64)
                                        }
                                    }
                            }
                        }
                    } else {
                        composableScope.launch(Dispatchers.Default) {
                            imageHelperMethods.convertBitmapToBase64(bitmap = bitmap)
                                .collect { base64 ->
                                    composableScope.launch(Dispatchers.Main) {
                                        listener(bitmap, base64)
                                    }
                                }
                        }
                    }
                }
            }
    }
}

/**
 * @param takeImageType pass TakeImageType.TAKE_IMAGE if you want to take a picture with camera and TakeImageType.TAKE_IMAGE_PREVIEW to take picture a preview
 * */
@Composable
private fun CameraPermissionForBase64(takeImageType: TakeImageType) {
    val context = LocalContext.current
    if (takeImageType == TakeImageType.TAKE_IMAGE) {
        takeCameraImageWithBase64Value =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (!success) {
                    photoUri = null
                }
            }
    }

    permissionCameraImageWithBase64Launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (takeImageType == TakeImageType.TAKE_IMAGE) {
                val photoFile = imageHelperMethods.createImageFile(context)
                val uri = photoFile.getUriWithFileProvider(context)
                photoUriWithBase64 = uri
                takeCameraImageWithBase64Value?.launch(uri)
            } else {
                takeCameraImagePreviewWithBase64Value?.launch(null)
            }
        }
    }
}

/**
 * This method is calling from listener to pick single image from camera with base64 values
 * @param context pass context
 * @param onPermanentCameraPermissionDeniedCallBack callback for permanent camera permission denied
 * */
fun takeSingleCameraImageWithBase64Value(
    context: Context,
    onPermanentCameraPermissionDeniedCallBack: (() -> Unit)? = null
) {
    if (shouldShowRequestPermissionRationale(
            context as Activity,
            Manifest.permission.CAMERA
        )
    ) {
        if (onPermanentCameraPermissionDeniedCallBack == null) {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            onPermanentCameraPermissionDeniedCallBack()
        }
    } else {
        permissionCameraImageWithBase64Launcher?.launch(Manifest.permission.CAMERA)
    }
}

/**
 * Callback for the single video to view
 * @param listener return the video to video player
 * */
@Composable
fun PickSingleVideo(
    listener: (Uri?) -> Unit
) {
    pickVideo =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                listener(uri)
            }
        }
}

/**
 * This method is calling from listener to pick single video from gallery
 * */
fun pickSingleVideo() {
    pickVideo?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
}


private fun isPickerAvailable(context: Context): Boolean =
    ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)