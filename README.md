# Image Picker Android

This library built to give to other developers an easy way to implement the image picker in Android
application with latest Android technologies.
Support me and I will appreciate if you provide me your feedback(s).<br />
Note: The example project doesn't contain all the examples for all methods.

The library contain/features:

- Picker for single image from gallery
- Picker for multiple images from gallery (up to 9 images)
- Take single image from camera (handled permission)
- Video Picker
- Support for the base64 value and scale (resize) are only for image
- All the previous features supported on Jetpack Compose too
- New updates coming soon and tell me your suggestions...

### Versioning

Gradle Version 8.1.3 <br />
Kotlin Version 1.9.20 <br />
JDK Version 17 <br />
Minimum SDK 24 <br />
Target SDK 34 <br />
Build Tool Version 34 <br />

## IMPORTANT NOTE

THE BETA RELEASES MAYBE CONTAIN MAJOR/MINOR CHANGES

## Basic Configuration

[![](https://jitpack.io/v/NicosNicolaou16/ImagePickerAndroid.svg)](https://jitpack.io/#NicosNicolaou16/ImagePickerAndroid)

```Groovy
implementation 'com.github.NicosNicolaou16:ImagePickerAndroid:2.0.4'
```

```Kotlin
implementation("com.github.NicosNicolaou16:ImagePickerAndroid:2.0.4")
```

```Groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

## Standard Configuration

### Step 1 - Get Instance

```Kotlin
class MainActivity : AppCompatActivity(), ImagePickerInterface {
    //...
    private var imagePicker: ImagePicker? = null

    //...
    fun initImagePicker() {
        //Builder
        //Note: fragmentActivity or fragment are mandatory one of them
        imagePicker = ImagePicker(
            fragmentActivity = this, //activity instance - private
            fragment = this, // fragment instance - private
            coroutineScope = lifecycleScope, // mandatory - coroutine scope from activity or fragment - private
            scaleBitmapModelForSingleImage = ScaleBitmapModel(
                height = 100,
                width = 100
            ), // optional, change the scale for image, by default is null
            scaleBitmapModelForMultipleImages = ScaleBitmapModel(
                height = 100,
                width = 100
            ), // optional, change the scale for image, by default is null
            scaleBitmapModelForCameraImage = ScaleBitmapModel(
                height = 100,
                width = 100
            ), // optional, change the scale for image, by default is null
            enabledBase64ValueForSingleImage = true, // optional, by default is false - private
            enabledBase64ValueForMultipleImages = true, // optional, by default is false - private
            enabledBase64ValueForCameraImage = true, // optional, by default is false - private
            imagePickerInterface = this // call back interface
        )
        //...other image picker initialization method(s)
    }
    //...
}
```

### Step 2 - Initialize the methods for Image Pickers (choose the preferred method(s))

```Kotlin
imagePicker?.initPickSingleImageFromGalleryResultLauncher()

imagePicker?.initPickMultipleImagesFromGalleryResultLauncher()

imagePicker?.initTakePhotoWithCameraResultLauncher()

imagePicker?.initPickSingleVideoFromGalleryResultLauncher()
```

### Step 3 Call from Click Listeners (choose the preferred method(s))

```Kotlin
imagePicker?.pickSingleImageFromGallery()

imagePicker?.pickMultipleImagesFromGallery()

imagePicker?.takeSinglePhotoWithCamera()

imagePicker?.pickSingleVideoFromGallery()
```

### Step 4 - Callbacks (Optionals)

```Kotlin
class MainActivity : AppCompatActivity(), ImagePickerInterface {
    //...
    override fun onGallerySingleImage(bitmap: Bitmap?, uri: Uri?) {
        super.onGalleryImage(bitmap, uri)
        //...your code here
    }

    override fun onCameraImage(bitmap: Bitmap?) {
        super.onCameraImage(bitmap)
        //...your code here
    }

    override fun onMultipleGalleryImages(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?
    ) {
        super.onMultipleGalleryImages(bitmapList, uriList)
        //...your code here
    }

    override fun onGallerySingleImageWithBase64Value(
        bitmap: Bitmap?,
        uri: Uri?,
        base64AsString: String?
    ) {
        super.onGalleryImage(bitmap, uri, base64AsString)
        //...your code here
    }

    override fun onCameraImageWithBase64Value(bitmap: Bitmap?, base64AsString: String?) {
        super.onCameraImage(bitmap, base64AsString)
        //...your code here
    }

    override fun onMultipleGalleryImagesWithBase64Value(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?,
        base64AsStringList: MutableList<String>?
    ) {
        super.onMultipleGalleryImages(bitmapList, uriList, base64AsStringList)
        //...your code here
    }

    override fun onGallerySingleVideo(uri: Uri?) {
        super.onGallerySingleVideo(uri)
        //...your code here
    }
}
```

## Compose Configuration

### Step 1 - Initialize the Callbacks (Optionals)

```Kotlin
PickSingleImage(
    scaleBitmapModel = ScaleBitmapModel(
        height = 100,
        width = 100
    ), listener = { bitmap, uri ->
        //...your code here
    })

PickSingleImageWithBase64Value(
    scaleBitmapModel = null,
    listener = { bitmap, uri, base64 ->
        //...your code here
    })

PickMultipleImages(
    scaleBitmapModel = null,
    listener = { bitmapList, uriList ->
        //...your code here
    })

PickMultipleImagesWithBase64Values(
    scaleBitmapModel = null,
    listener = { bitmapList, uriList, base64List ->
        //...your code here
    })

TakeSingleCameraImage(
    scaleBitmapModel = null,
    listener = { bitmap, uri ->
        //...your code here
    })

TakeSingleCameraImageWithBase64Value(
    scaleBitmapModel = null,
    listener = { bitmap, uri, base64 ->
        //...your code here
    })

PickSingleVideo(listener = { uri ->
    //...your code here
})
```

### Step 2 Call from Click Listeners (choose the preferred method(s))

```Kotlin
pickSingleImage()

pickSingleImageWithBase64Value()

pickMultipleImages()

pickMultipleImagesWithBase64Values()

takeSingleCameraImage()

takeCameraImageWithBase64Value()

pickSingleVideo()
```

### Example for Compose Implementation

```kotlin
@Composable
fun ImagePicker() {
    val context = LocalContext.current
    val bitmapValue = remember {
        mutableStateOf(Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))
    }
    //Initialize the call back
    PickSingleImage(scaleBitmapModel = null, listener = { bitmap, uri ->
        if (bitmap != null) {
            bitmapValue.value = bitmap
        }
    })
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        //other code
        Button(modifier = Modifier.size(150.dp, 50.dp), onClick = { 
            //pick image from the gallery 
            pickSingleImage() 
        }) {
            Text(
                text = stringResource(R.string.pick_single_image),
                style = TextStyle(textAlign = TextAlign.Center)
            )
        }
        //other code
    }
}
```