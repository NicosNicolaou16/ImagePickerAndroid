package com.nicos.imagepickerandroid.image_picker

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.nicos.imagepickerandroid.utils.image_helper_methods.ImageHelperMethods
import com.nicos.imagepickerandroid.utils.image_helper_methods.ScaleBitmapModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private var imageHelperMethods = ImageHelperMethods()
private var pickSingleImage: ManagedActivityResultLauncher<String, Uri?>? = null
private var pickSingleImageWithBase64Value: ManagedActivityResultLauncher<String, Uri?>? = null
private var pickMultipleImages: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>? =
    null
private var pickMultipleImagesWithBase64Values: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>? =
    null
private var takeCameraImage: ManagedActivityResultLauncher<Void?, Bitmap?>? = null
private var takeCameraImageWithBase64Value: ManagedActivityResultLauncher<Void?, Bitmap?>? = null
private var pickVideo: ManagedActivityResultLauncher<String, Uri?>? = null

@Composable
fun pickSingleImage(
    scaleBitmapModel: ScaleBitmapModel?,
    listener: (Bitmap?, Uri?) -> Unit
) {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    pickSingleImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
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

fun pickSingleImage() {
    pickSingleImage?.launch("image/*")
}

@Composable
fun pickSingleImageWithBase64Value(
    scaleBitmapModel: ScaleBitmapModel?,
    listener: (Bitmap?, Uri?, String?) -> Unit
) {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    pickSingleImageWithBase64Value =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
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

fun pickSingleImageWithBase64Value() {
    pickSingleImageWithBase64Value?.launch("image/*")
}

@Composable
fun pickMultipleImages(
    scaleBitmapModel: ScaleBitmapModel?,
    listener: (MutableList<Bitmap>?, MutableList<Uri>?) -> Unit
) {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    pickMultipleImages =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uriList ->
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

fun pickMultipleImages() {
    pickMultipleImages?.launch("image/*")
}

@Composable
fun pickMultipleImagesWithBase64Values(
    scaleBitmapModel: ScaleBitmapModel?,
    listener: (MutableList<Bitmap>?, MutableList<Uri>?, MutableList<String>?) -> Unit
) {
    val context = LocalContext.current
    val composableScope = rememberCoroutineScope()
    pickMultipleImagesWithBase64Values =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { uriList ->
            composableScope.launch(Dispatchers.Main) {
                val bitmapList = mutableListOf<Bitmap>()
                withContext(Dispatchers.Default) {
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
}

fun pickMultipleImagesWithBase64Value() {
    pickMultipleImagesWithBase64Values?.launch("image/*")
}

@Composable
fun takeCameraImage(
    scaleBitmapModel: ScaleBitmapModel?,
    listener: (Bitmap?) -> Unit
) {
    val composableScope = rememberCoroutineScope()
    takeCameraImage =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                if (scaleBitmapModel != null) {
                    composableScope.launch(Dispatchers.Default) {
                        imageHelperMethods.scaleBitmap(
                            bitmap = bitmap,
                            scaleBitmapModel = scaleBitmapModel
                        ).collect { scaledBitmap ->
                            composableScope.launch(Dispatchers.Main) {
                                listener(scaledBitmap)
                            }
                        }
                    }
                } else {
                    listener(bitmap)
                }
            }
        }
}

fun takeCameraImage(context: Context) {
    takeCameraImage?.launch(null)
}

@Composable
fun takeCameraImageWithBase64Value(
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

fun takeCameraImageWithBase64Value() {
    takeCameraImageWithBase64Value?.launch(null)
}

@Composable
fun pickVideo(
    listener: (Bitmap?, Uri?) -> Unit
) {
    val context = LocalContext.current
    pickVideo =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            var bitmap: Bitmap? = null
            if (uri != null) {
                bitmap = imageHelperMethods.convertUriToBitmap(
                    contentResolver = context.contentResolver,
                    uri = uri
                )
                listener(bitmap, uri)
            }
        }
}

fun pickVideo() {
    pickVideo?.launch("video/*")
}