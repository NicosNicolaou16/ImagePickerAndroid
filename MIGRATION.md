## 🚚 Migration Guide

> [!IMPORTANT]  
> ## Breaking changes from the version 2.3.0 and higher <br /> <br />
> `takeSingleCameraImage()` changed to `takeSingleCameraImage(context = context)` <br /> <br />
> `takeSingleCameraImageWithBase64Value()` changed to
`takeSingleCameraImageWithBase64Value(context = context)`

> [!IMPORTANT]  
> ## Breaking changes from the version 2.4.0 and higher <br /> <br />
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
> ## Breaking changes from the version 2.5.0 and higher <br /> <br />
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

> [!IMPORTANT]  
> ## Breaking changes from the version 2.5.6 and higher <br /> <br />
>
> Activity/Fragment/XML support <br />
>
> Implemented a check to ensure the image picker is available; no migration required. <br />
> Added a new optional callback `fun onImagePickerNotAvailable() { super.onImagePickerNotAvailable() }` <br />
>
> Compose Support <br />
>
> Removed the **context** parameter from the Image Picker methods, leaving only the optional listener parameter. <br />
> - context <- NO NEED TO PASS THIS ANYMORE, SO REMOVE THE PARAMETER <br />
> - onImagePickerNotAvailable (optional) <br />
>
> Note for both types (Activity/Fragment/XML/Compose support): When the image is not available there is a Log.w(...), show only when the BuildConfig.DEBUG is true. <br />
```logcatfilter
ImagePickerAndroid      com.nicos.imagepickerandroidcompose  W  Image Picker is not available
```

```Kotlin
pickSingleImage(onImagePickerNotAvailable = {
  // show custom dialog - showDialog.value = true
})
pickSingleImageWithBase64Value(onImagePickerNotAvailable = {
  // show custom dialog - showDialog.value = true
})
pickMultipleImages(onImagePickerNotAvailable = {
  // show custom dialog - showDialog.value = true
})
pickMultipleImagesWithBase64Values(onImagePickerNotAvailable = {
  // show custom dialog - showDialog.value = true
})
```