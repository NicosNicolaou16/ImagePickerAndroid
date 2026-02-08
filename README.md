# Image Picker Android

[![](https://jitpack.io/v/NicosNicolaou16/ImagePickerAndroid.svg)](https://jitpack.io/#NicosNicolaou16/ImagePickerAndroid)


A modern and easy-to-use Android library for picking images and videos from the gallery or capturing
them with the camera. It offers a unified API for traditional Views (Activities/Fragments with XML)
and Jetpack Compose. <br />

Note: The example project does not include examples for all methods. <br />

## 🌟 Features

This library is designed to simplify media selection in your Android app with a robust set of
features:

* **🖼️ Single Image Picker**: Select a single image from the gallery.
* **🎨 Multiple Image Picker**: Choose multiple images (up to 9).
* **📸 Camera Capture**: Capture a new photo, with streamlined permission handling that can direct
  users to app settings or can use the onPermanentCameraPermissionDenied() callback to implement
  your own custom logic).
* **📹 Video Picker**: Select a single video from the gallery.
* **🔄 Base64 Conversion**: Automatically convert selected images to a Base64 string.
* **✂️ Image Scaling**: Easily resize images to your desired dimensions.
* **🚀 Jetpack Compose Support**: First-class support for Jetpack Compose, with dedicated
  composables.

---

## 🤔 Why Use This Library?

* **Unified API**: Supports both traditional Views (Activities/Fragments) and modern Jetpack Compose
  UIs.
* **Time-Saving**: Provides a simple, out-of-the-box solution to a common Android task, saving you
  significant development time.
* **Boilerplate Reduction**: Handles `ActivityResultLauncher`, permissions, and file processing,
  letting you focus on your app's logic.
* **Advanced Functionality**: Includes powerful features like Base64 encoding and image scaling
  without needing extra dependencies.

---

### 🛠️ Versioning

*   **JDK Version**: `17`
*   **Target SDK**: `36`
*   **Minimum SDK**: `29`
*   **Kotlin Version**: `2.3.10`
*   **Gradle Version**: `9.0.0`
*   **Build Tool Version**: `36.0.0`

---

## !! IMPORTANT NOTE

THE BETA RELEASES MAY CONTAIN MAJOR OR MINOR CHANGES. <br /> <br />

---

## 🚚 Migration Guide

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
> [!IMPORTANT]  
> Breaking changes from the version 2.5.0 and higher <br /> <br />
>
> Activity/Fragment/XML support <br />
> 
> Implemented a check to ensure the image picker is available; no migration required. <br />
> Added a new optional callback `fun onImagePickerNotAvailable() { super.onImagePickerNotAvailable() }` <br />
> 
> Compose Support <br />
>
> Added two new parameters to the Image Picker methods called in listener, one parameter is required, the second is optional. <br />
> - context (required) <br />
> - onImagePickerNotAvailable (optional) <br />
> 
> Note for both types (Activity/Fragment/XML/Compose support): When the image is not available there is a Log.w(...), show only when the BuildConfig.DEBUG is true. <br />
```logcatfilter
ImagePickerAndroid      com.nicos.imagepickerandroidcompose  W  Image Picker is not available
```

```Kotlin
pickSingleImage(context = context, onImagePickerNotAvailable = {
  // show custom dialog - showDialog.value = true
})
pickSingleImageWithBase64Value(context = context, onImagePickerNotAvailable = {
  // show custom dialog - showDialog.value = true
})
pickMultipleImages(context = context, onImagePickerNotAvailable = {
  // show custom dialog - showDialog.value = true
})
pickMultipleImagesWithBase64Values(context = context, onImagePickerNotAvailable = {
  // show custom dialog - showDialog.value = true
})
```

---

## ⚙️ Basic Configuration (Gradle Dependencies)

> [!IMPORTANT]  
> Check my article with the implementation <br />
> :point_right: [ImagePickerAndroid - My Android Image Picker Library 🧑‍💻 - Medium](https://medium.com/@nicosnicolaou/imagepickerandroid-my-android-image-picker-library-d1ac86c60e3a) :point_left: <br />

### Groovy

```Groovy
implementation 'com.github.NicosNicolaou16:ImagePickerAndroid:2.5.4'
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
implementation("com.github.NicosNicolaou16:ImagePickerAndroid:2.5.4")
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
imagePickerAndroid = "2.5.4"

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

---

## 🚀 Standard Configuration (XML)

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
        //...your code here
    }

    override fun onImagePickerNotAvailable() {
      super.onImagePickerNotAvailable()
      //...your code here
    }
}
```

---

## 🚀 Compose Configuration

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
pickSingleImage(context = context, onImagePickerNotAvailable = {
      // show custom dialog - showDialog.value = true
})

pickSingleImageWithBase64Value(context = context, onImagePickerNotAvailable = {
    // show custom dialog - showDialog.value = true 
})    

pickMultipleImages(context = context, onImagePickerNotAvailable = {
    // show custom dialog - showDialog.value = true
})

pickMultipleImagesWithBase64Values(context = context, onImagePickerNotAvailable = {
    // show custom dialog - showDialog.value = true
})

/**
 * onPermanentCameraPermissionDeniedCallBack is optional
 * */
takeSingleCameraImage(context = context, onPermanentCameraPermissionDeniedCallBack {
    // show custom dialog - showDialog.value = true
})

takeSingleCameraImageWithBase64Value(context = context, onPermanentCameraPermissionDeniedCallBack {
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
            pickSingleImage(context = context, onImagePickerNotAvailable = {})
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

---

## ⭐ Stargazers

If you enjoy this project, please give it a star!
Check out all the stargazers
here: [Stargazers on GitHub](https://github.com/NicosNicolaou16/ImagePickerAndroid/stargazers)

---

## 🙏 Support & Contributions

This library is actively maintained. Feedback, bug reports, and feature requests are welcome! Please feel free to **open an issue** or submit a **pull request**.