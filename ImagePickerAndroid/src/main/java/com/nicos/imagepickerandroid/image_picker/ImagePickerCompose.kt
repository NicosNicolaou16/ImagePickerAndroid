package com.nicos.imagepickerandroid.image_picker

import android.Manifest
import android.app.Activity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.nicos.imagepickerandroid.utils.image_helper_methods.ImageHelperMethods
import com.nicos.imagepickerandroid.utils.image_helper_methods.ScaleBitmapModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>? = null
private var imageHelperMethods = ImageHelperMethods()
private var pickSingleImage: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? = null
private var pickSingleImageWithBase64Value: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? =
    null
private var pickMultipleImages: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>? =
    null
private var pickMultipleImagesWithBase64Values: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>? =
    null
private var takeCameraImage: ManagedActivityResultLauncher<Void?, Bitmap?>? = null
private var takeCameraImageWithBase64Value: ManagedActivityResultLauncher<Void?, Bitmap?>? = null
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
 * */
fun pickSingleImage() {
    pickSingleImage?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
 * */
fun pickSingleImageWithBase64Value() {
    pickSingleImageWithBase64Value?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
 * */
fun pickMultipleImages() {
    pickMultipleImages?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
 * */
fun pickMultipleImagesWithBase64Values() {
    pickMultipleImagesWithBase64Values?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}

/**
 * Callback for the single image to view from camera
 * @param scaleBitmapModel pass ScaleBitmapModel with height and width to resize an image
 * @param listener return the image view and uri
 * */
@Composable
fun TakeSingleCameraImage(
    scaleBitmapModel: ScaleBitmapModel?,
    listener: (Bitmap?, Uri?) -> Unit
) {
    CameraPermission()
    val composableScope = rememberCoroutineScope()
    takeCameraImage =
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

@Composable
fun CameraPermission() {
    permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) takeCameraImage?.launch(null)
    }
}

/**
 * This method is calling from listener to pick single image from camera
 * */
fun takeSingleCameraImage(context: Context, onPermanentCameraPermissionDeniedCallBack: (() -> Unit)? = null) {
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
        permissionLauncher?.launch(Manifest.permission.CAMERA)
    }
}

/**
 * Callback for the single image to view from camera with base64 value
 * @param scaleBitmapModel pass ScaleBitmapModel with height and width to resize an image
 * @param listener return the images to view and base64 value
 * */
@Composable
fun TakeSingleCameraImageWithBase64Value(
    scaleBitmapModel: ScaleBitmapModel?,
    listener: (Bitmap?, String?) -> Unit
) {
    val composableScope = rememberCoroutineScope()
    takeCameraImageWithBase64Value =
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

/**
 * This method is calling from listener to pick single image from camera with base64 values
 * */
fun takeSingleCameraImageWithBase64Value(context: Context, onPermanentCameraPermissionDeniedCallBack: (() -> Unit)? = null) {
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
        takeCameraImageWithBase64Value?.launch(null)
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