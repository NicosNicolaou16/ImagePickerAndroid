# Image Picker Android

This library is built to provide other developers with an easy way to implement an image picker in
Android applications using the latest Android technologies. It supports Activities, Fragments with
XML, and Jetpack Compose.
Support me, and I would appreciate any feedback you provide. <br />
Note: The example project does not include examples for all methods.

The library contains/features:

- Picker for a single image from the gallery.
- Picker for multiple images from the gallery (up to 9 images).
- Camera picker for a single image (with permission handling - manage the scenario where camera
  permission is permanently denied by directing the user to the app settings to modify the
  permission or can use the onPermanentCameraPermissionDenied() callback to implement your own
  custom logic).
- Video picker.
- Retrieve the base64 value.
- Image scaling (resize) – available only for images.
- All of the above features are also supported in Jetpack Compose.
- New updates coming soon! Feel free to share your suggestions.

Reasons to use this library

- It supports both Activity and Fragment with XML, as well as Jetpack Compose, ensuring
  compatibility with various Android development approaches.
- The library provides a user-friendly way to integrate image picking functionalities, saving time
  and effort.
- It offers advanced features like base64 encoding support and image scaling, enhancing your app's
  image handling capabilities.

### Versioning

Gradle Version 8.10.1 <br />
Kotlin Version 2.1.21 <br />
JDK Version 17 <br />
Minimum SDK 24 <br />
Target SDK 35 <br />
Build Tool Version 35.0.1 <br />

## IMPORTANT NOTE

THE BETA RELEASES MAY CONTAIN MAJOR OR MINOR CHANGES. <br /> <br />

> [!IMPORTANT]  
> Breaking changes from the version 2.3.0 and higher <br /> <br />
> `takeSingleCameraImage()` changed to `takeSingleCameraImage(context = context)` <br /> <br />
> `takeSingleCameraImageWithBase64Value()` changed to
`takeSingleCameraImageWithBase64Value(context = context)`

> [!IMPORTANT]  
> Breaking changes from the version 2.4.0 and higher <br /> <br />
> Added a new optional parameter `takeImageType: TakeImageType = TakeImageType.TAKE_IMAGE` (By
> default is `TakeImageType.TAKE_IMAGE`) <br /> <br />
> Those parameters are used to select whether you want `ActivityResultContracts.TakePicture()` or
> `ActivityResultContracts.TakePicturePreview()`. <br /> <br />
> The two options (enum) are `TakeImageType.TAKE_IMAGE` or
`TakeImageType.TAKE_IMAGE_PREVIEW` <br /> <br />

```Kotlin
// The two options (enum) are `TakeImageType.TAKE_IMAGE` or `TakeImageType.TAKE_IMAGE_PREVIEW`,
// by default is `TakeImageType.TAKE_IMAGE`
TakeSingleCameraImage(
    scaleBitmapModel = null,
    takeImageType = TakeImageType.TAKE_IMAGE,
    listener = { bitmap, uri ->
//...your code here
    })

// The two options (enum) are `TakeImageType.TAKE_IMAGE` or `TakeImageType.TAKE_IMAGE_PREVIEW`,
// by default is `TakeImageType.TAKE_IMAGE`
TakeSingleCameraImageWithBase64Value(
    scaleBitmapModel = null,
    takeImageType = TakeImageType.TAKE_IMAGE,
    listener = { bitmap, uri, base64 ->
//...your code here
    })
```

## Basic Configuration (Gradle Dependencies)

[![](https://jitpack.io/v/NicosNicolaou16/ImagePickerAndroid.svg)](https://jitpack.io/#NicosNicolaou16/ImagePickerAndroid)

> [!IMPORTANT]  
> Check my article with the implementation <br />
> :
>
point_right: [ImagePickerAndroid - My Android Image Picker Library 🧑‍💻 - Medium](https://medium.com/@nicosnicolaou/imagepickerandroid-my-android-image-picker-library-d1ac86c60e3a)
> :point_left: <br />

### Groovy

```Groovy
implementation 'com.github.NicosNicolaou16:ImagePickerAndroid:2.4.0'
```

```Groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

### Kotlin DSL

```Kotlin
implementation("com.github.NicosNicolaou16:ImagePickerAndroid:2.4.0")
```

```Kotlin
dependencyResolutionManagement {
    //...
    repositories {
        //...
        maven { url = uri("https://jitpack.io") }
    }
}
```

### libs.versions.toml

```toml
[versions]
# other versions here...
imagePickerAndroid = "2.4.0"

[libraries]
# other libraries here...
image-picker-android = { group = "com.github.NicosNicolaou16", name = "ImagePickerAndroid", version.ref = "imagePickerAndroid" }
```

```Kotlin
implementation(libs.image.picker.android)
```

```Kotlin
dependencyResolutionManagement {
    //...
    repositories {
        //...
        maven { url = uri("https://jitpack.io") }
    }
}
```

## Standard Configuration (XML)

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
            imagePickerInterface = this, // call back interface
            shouldRedirectedToSettingsIfPermissionDenied = false // optional, by default is true - private, if it set false, need to call the callback onPermanentCameraPermissionDenied()
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

    // Need to call and set the shouldRedirectedToSettingsIfPermissionDenied = false from builder to use this callback
    override fun onPermanentCameraPermissionDenied() {
        super.onPermanentCameraPermissionDenied()
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

// The two options (enum) are `TakeImageType.TAKE_IMAGE` or `TakeImageType.TAKE_IMAGE_PREVIEW`,
// by default is `TakeImageType.TAKE_IMAGE`
TakeSingleCameraImage(
    scaleBitmapModel = null,
    takeImageType = TakeImageType.TAKE_IMAGE,
    listener = { bitmap, uri ->
        //...your code here
    })

// The two options (enum) are `TakeImageType.TAKE_IMAGE` or `TakeImageType.TAKE_IMAGE_PREVIEW`,
// by default is `TakeImageType.TAKE_IMAGE`
TakeSingleCameraImageWithBase64Value(
    scaleBitmapModel = null,
    takeImageType = TakeImageType.TAKE_IMAGE,
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

/**
 * onPermanentCameraPermissionDeniedCallBack is optional
 * */
takeSingleCameraImage(context = context, onPermanentCameraPermissionDeniedCallBack {
    // show custom dialog - showDialog.value = true
})

takeSingleCameraImageWithBase64Value(context = onPermanentCameraPermissionDeniedCallBack {
    // show custom dialog - showDialog.value = true
})

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