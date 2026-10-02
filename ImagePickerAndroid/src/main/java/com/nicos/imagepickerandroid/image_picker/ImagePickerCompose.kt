package com.nicos.imagepickerandroid.image_picker

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.nicos.imagepickerandroid.model.DecodedImages
import com.nicos.imagepickerandroid.utils.constants.Constants.imagePickerNotAvailableLogs
import com.nicos.imagepickerandroid.utils.enums.TakeImageType
import com.nicos.imagepickerandroid.utils.extensions.findActivity
import com.nicos.imagepickerandroid.utils.extensions.getUriWithFileProvider
import com.nicos.imagepickerandroid.utils.image_helper_methods.ImageHelperMethods
import com.nicos.imagepickerandroid.utils.image_helper_methods.ScaleBitmapModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** launcher for camera permission */
private var permissionLauncherCameraImage: ManagedActivityResultLauncher<String, Boolean>? = null

/** launcher for camera permission with base64 value */
private var permissionCameraImageWithBase64Launcher: ManagedActivityResultLauncher<String, Boolean>? =
    null


/** instance for image helper methods */
private var imageHelperMethods = ImageHelperMethods()

/** launcher for single image from gallery */
private var pickSingleImage: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? = null

/** launcher for single image from gallery with base64 value */
private var pickSingleImageWithBase64Value: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? =
    null

/** launcher for multiple images from gallery */
private var pickMultipleImages: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>? =
    null

/** launcher for multiple images from gallery with base64 values */
private var pickMultipleImagesWithBase64Values: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>? =
    null

/** launcher for single image from camera */
private var takeCameraImagePreview: ManagedActivityResultLauncher<Void?, Bitmap?>? = null

/** launcher for single image preview from camera */
private var takeCameraImage: ManagedActivityResultLauncher<Uri, Boolean>? = null

/** launcher for single image from camera with base64 value */
private var takeCameraImagePreviewWithBase64Value: ManagedActivityResultLauncher<Void?, Bitmap?>? =
    null

/** launcher for single image preview from camera with base64 value */
private var takeCameraImageWithBase64Value: ManagedActivityResultLauncher<Uri, Boolean>? = null

/** launcher for single video from gallery */
private var pickVideo: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? = null

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
            composableScope.launch(Dispatchers.IO) {
                if (uri != null) {
                    bitmap = imageHelperMethods.convertUriToBitmap(
                        contentResolver = context.contentResolver,
                        uri = uri
                    )
                }
                if (scaleBitmapModel != null) {
                    imageHelperMethods.scaleBitmap(
                        bitmap = bitmap,
                        scaleBitmapModel = scaleBitmapModel
                    ).collect { scaledBitmap ->
                        composableScope.launch(Dispatchers.Main) {
                            listener(scaledBitmap, uri)
                        }
                    }
                } else {
                    composableScope.launch(Dispatchers.Main) {
                        listener(bitmap, uri)
                    }
                }
            }
        }
}

/**
 * This method is calling from listener to pick single image
 * @param onImagePickerNotAvailable callback for image picker not available
 * */
fun pickSingleImage(
    onImagePickerNotAvailable: (() -> Unit)? = null
) {
    try {
        pickSingleImage?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    } catch (e: ActivityNotFoundException) {
        // Catch the exception in case the picker is not available at runtime
        e.printStackTrace()
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
            //val bitmap: Bitmap?
            composableScope.launch {
                if (uri != null) {
                    val bitmap = withContext(Dispatchers.IO) {
                        imageHelperMethods.convertUriToBitmap(
                            contentResolver = context.contentResolver,
                            uri = uri
                        )
                    }
                    if (scaleBitmapModel != null) {
                        composableScope.launch(Dispatchers.IO) {
                            imageHelperMethods.scaleBitmap(
                                bitmap = bitmap,
                                scaleBitmapModel = scaleBitmapModel
                            ).collect { scaledBitmap ->
                                imageHelperMethods.convertBitmapToBase64(bitmap = scaledBitmap)
                                    .collect { base64 ->
                                        composableScope.launch(Dispatchers.Main) {
                                            listener(scaledBitmap, uri, base64)
                                        }
                                    }
                            }
                        }
                    } else {
                        composableScope.launch(Dispatchers.IO) {
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
}

/**
 * This method is calling from listener to pick single image with base64 value
 * @param onImagePickerNotAvailable callback for image picker not available
 * */
fun pickSingleImageWithBase64Value(
    onImagePickerNotAvailable: (() -> Unit)? = null
) {
    try {
        pickSingleImageWithBase64Value?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    } catch (e: ActivityNotFoundException) {
        // Catch the exception in case the picker is not available at runtime
        e.printStackTrace()
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
            composableScope.launch(Dispatchers.IO) {
                val decoded: DecodedImages = withContext(Dispatchers.IO) {
                    imageHelperMethods.decodeUrisToBitmaps(
                        contentResolver = context.contentResolver,
                        uris = uriList
                    )
                }
                if (scaleBitmapModel != null) {
                    imageHelperMethods.scaleBitmapList(
                        bitmapList = decoded.bitmaps,
                        scaleBitmapModel = scaleBitmapModel
                    ).collect { scaledBitmapList ->
                        composableScope.launch(Dispatchers.Main) {
                            listener(scaledBitmapList, decoded.uris.toMutableList())
                        }
                    }
                } else {
                    composableScope.launch(Dispatchers.Main) {
                        listener(decoded.bitmaps, decoded.uris.toMutableList())
                    }
                }
            }
        }
}

/**
 * This method is calling from listener to pick multiple images
 * @param onImagePickerNotAvailable callback for image picker not available
 * */
fun pickMultipleImages(
    onImagePickerNotAvailable: (() -> Unit)? = null
) {
    try {
        pickMultipleImages?.launch(input = PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    } catch (e: ActivityNotFoundException) {
        // Catch the exception in case the picker is not available at runtime
        e.printStackTrace()
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
                val decoded: DecodedImages = withContext(Dispatchers.IO) {
                    imageHelperMethods.decodeUrisToBitmaps(
                        contentResolver = context.contentResolver,
                        uris = uriList
                    )
                }
                if (scaleBitmapModel != null) {
                    imageHelperMethods.scaleBitmapList(
                        bitmapList = decoded.bitmaps,
                        scaleBitmapModel = scaleBitmapModel
                    ).collect { scaledBitmapList ->
                        imageHelperMethods.convertListOfBitmapsToListOfBase64(bitmapList = scaledBitmapList)
                            .collect { base64List ->
                                composableScope.launch(Dispatchers.Main) {
                                    listener(
                                        scaledBitmapList,
                                        decoded.uris.toMutableList(),
                                        base64List
                                    )
                                }
                            }
                    }
                } else {
                    imageHelperMethods.convertListOfBitmapsToListOfBase64(bitmapList = decoded.bitmaps)
                        .collect { base64List ->
                            composableScope.launch(Dispatchers.Main) {
                                listener(decoded.bitmaps, decoded.uris.toMutableList(), base64List)
                            }
                        }
                }
            }
        }
}

/**
 * This method is calling from listener to pick multiple images with base64 values
 * @param onImagePickerNotAvailable callback for image picker not available
 * */
fun pickMultipleImagesWithBase64Values(
    onImagePickerNotAvailable: (() -> Unit)? = null
) {
    try {
        pickMultipleImagesWithBase64Values?.launch(input = PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    } catch (e: ActivityNotFoundException) {
        // Catch the exception in case the picker is not available at runtime
        e.printStackTrace()
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
    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val composableScope = rememberCoroutineScope()
    if (takeImageType == TakeImageType.TAKE_IMAGE) {
        takeCameraImage =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    if (photoUri != null) {
                        composableScope.launch(Dispatchers.IO) {
                            val bitmap =
                                imageHelperMethods.convertUriToBitmap(
                                    contentResolver = context.contentResolver,
                                    photoUri
                                )
                            if (scaleBitmapModel != null) {

                                imageHelperMethods.scaleBitmap(
                                    bitmap = bitmap,
                                    scaleBitmapModel = scaleBitmapModel
                                ).collect { scaledBitmap ->
                                    composableScope.launch(Dispatchers.Main) {
                                        listener(scaledBitmap, photoUri)
                                    }
                                }
                            } else {
                                composableScope.launch(Dispatchers.Main) {
                                    listener(bitmap, photoUri)
                                }
                            }
                        }
                    }
                }
            }
    } else {
        takeCameraImagePreview =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) { bitmap ->
                composableScope.launch(Dispatchers.IO) {
                    if (bitmap != null) {
                        val uri: Uri =
                            imageHelperMethods.getUriFromBitmap(context = context, bitmap = bitmap)
                        if (scaleBitmapModel != null) {
                            imageHelperMethods.scaleBitmap(
                                bitmap = bitmap,
                                scaleBitmapModel = scaleBitmapModel
                            ).collect { scaledBitmap ->
                                composableScope.launch(Dispatchers.Main) {
                                    listener(scaledBitmap, uri)
                                }
                            }
                        } else {
                            composableScope.launch(Dispatchers.Main) {
                                listener(bitmap, uri)
                            }
                        }
                    }
                }
            }
    }
    CameraPermission(takeImageType = takeImageType, onUriCreated = { uri -> photoUri = uri })
}

/**
 * @param takeImageType pass TakeImageType.TAKE_IMAGE if you want to take a picture with camera and TakeImageType.TAKE_IMAGE_PREVIEW to take picture a preview
 * @param onUriCreated called with the file Uri the camera will write into, before the camera is launched
 * */
@Composable
private fun CameraPermission(
    takeImageType: TakeImageType,
    onUriCreated: (Uri) -> Unit
) {
    val context = LocalContext.current
    permissionLauncherCameraImage = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (takeImageType == TakeImageType.TAKE_IMAGE) {
                val photoFile = imageHelperMethods.createImageFile(context)
                val uri = photoFile.getUriWithFileProvider(context)
                onUriCreated(uri)
                takeCameraImage?.launch(input = uri)
            } else {
                takeCameraImagePreview?.launch(input = null)
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
    requestCameraPermission(
        context = context,
        permissionLauncher = permissionLauncherCameraImage,
        onPermanentCameraPermissionDeniedCallBack = onPermanentCameraPermissionDeniedCallBack
    )
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
    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    if (takeImageType == TakeImageType.TAKE_IMAGE) {
        takeCameraImageWithBase64Value =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    if (photoUri != null) {
                        if (scaleBitmapModel != null) {
                            composableScope.launch(context = Dispatchers.Default) {
                                val bitmap = imageHelperMethods.convertUriToBitmap(
                                    contentResolver = context.contentResolver,
                                    uri = photoUri
                                )
                                imageHelperMethods.scaleBitmap(
                                    bitmap = bitmap,
                                    scaleBitmapModel = scaleBitmapModel
                                ).collect { scaledBitmap ->
                                    imageHelperMethods.convertBitmapToBase64(bitmap = scaledBitmap)
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
                                    uri = photoUri
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
                                imageHelperMethods.convertBitmapToBase64(bitmap = scaledBitmap)
                                    .collect { base64 ->
                                        composableScope.launch(context = Dispatchers.Main) {
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

    CameraPermissionForBase64(
        takeImageType = takeImageType,
        onUriCreated = { uri -> photoUri = uri })
}

/**
 * @param takeImageType pass TakeImageType.TAKE_IMAGE if you want to take a picture with camera and TakeImageType.TAKE_IMAGE_PREVIEW to take picture a preview
 * */
@Composable
private fun CameraPermissionForBase64(
    takeImageType: TakeImageType,
    onUriCreated: (Uri) -> Unit
) {
    val context = LocalContext.current
    permissionCameraImageWithBase64Launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (takeImageType == TakeImageType.TAKE_IMAGE) {
                val photoFile = imageHelperMethods.createImageFile(context)
                val uri = photoFile.getUriWithFileProvider(context)
                onUriCreated(uri)
                takeCameraImageWithBase64Value?.launch(input = uri)
            } else {
                takeCameraImagePreviewWithBase64Value?.launch(input = null)
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
    requestCameraPermission(
        context = context,
        permissionLauncher = permissionLauncherCameraImage,
        onPermanentCameraPermissionDeniedCallBack = onPermanentCameraPermissionDeniedCallBack
    )
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
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                listener(uri)
            }
        }
}

/**
 * This method is calling from listener to pick single video from gallery
 * */
fun pickSingleVideo(onImagePickerNotAvailable: (() -> Unit)? = null) {
    try {
        pickVideo?.launch(input = PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.VideoOnly))
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        imagePickerNotAvailableLogs()
        onImagePickerNotAvailable?.invoke()
    }
}

/**
 * Shared logic for both camera entry points.
 * Keeps the existing behavior: if rationale should be shown → callback, or App Info when the callback is null.
 * */
private fun requestCameraPermission(
    context: Context,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>?,
    onPermanentCameraPermissionDeniedCallBack: (() -> Unit)?
) {
    val activity = context.findActivity()
    val shouldShowRationale = activity != null &&
            shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)

    if (shouldShowRationale) {
        if (onPermanentCameraPermissionDeniedCallBack == null) {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            onPermanentCameraPermissionDeniedCallBack()
        }
    } else {
        permissionLauncher?.launch(Manifest.permission.CAMERA)
    }
}