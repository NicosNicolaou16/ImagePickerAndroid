# Image Picker Android

### Versioning

Gradle Version 8.0.1 <br />
Kotlin Version 1.8.21 <br />
JDK Version 17 <br />
Minimum SDK 24 <br />
Target SDK 33 <br />

## IMPORTANT NOTE
THE NEXT BETA RELEASES MAYBE CONTAIN MAJOR/MINOR CHANGES

## Configuration

[![](https://jitpack.io/v/NicosNicolaou16/ImagePickerAndroid.svg)](https://jitpack.io/#NicosNicolaou16/ImagePickerAndroid)

### Step 1

```Groovy
implementation 'com.github.NicosNicolaou16:ImagePickerAndroid:1.0.0-beta1'
```

```Groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

### Step 2 - Initialize the methods for Image Pickers (choose the preferred methods)

```Kotlin
initPickAnImageFromGalleryResultLauncher(
    fragmentActivity = this,
    imagePickerInterface = this
)

initPickMultipleImagesFromGalleryResultLauncher(
    fragmentActivity = this,
    coroutineScope = lifecycleScope,
    imagePickerInterface = this
)

initTakePhotoWithCameraResultLauncher(
    fragmentActivity = this,
    imagePickerInterface = this
)
```

### Step 3 Call from Click Listeners (choose the preferred methods)

```Kotlin
pickAnImageFromGallery(fragmentActivity = this)

pickMultipleImagesFromGallery(fragmentActivity = this)

takeAPhotoWithCamera(fragmentActivity = this)
```

### Step 4 - Callbacks (Optional)

```Kotlin
class MainActivity : AppCompatActivity(), ImagePickerInterface {
    //...
}

override fun onGalleryImage(bitmap: Bitmap?, uri: Uri?) {
    super.onGalleryImage(bitmap, uri)
}

override fun onCameraImage(bitmap: Bitmap?, uri: Uri?) {
    super.onCameraImage(bitmap, uri)
}

override fun onMultipleGalleryImages(
    bitmapList: MutableList<Bitmap>?,
    uriList: MutableList<Uri>?
) {
    super.onMultipleGalleryImages(bitmapList, uriList)
    //Main Thread
    lifecycleScope.launch(Dispatchers.Main) {
        //...your code here
    }
}
```