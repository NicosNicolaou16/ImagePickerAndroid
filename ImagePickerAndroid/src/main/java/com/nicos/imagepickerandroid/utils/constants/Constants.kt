package com.nicos.imagepickerandroid.utils.constants

import android.util.Log
import com.nick.imagepickerandroid.BuildConfig

internal object Constants {
    internal const val TAG = "ImagePickerAndroid"

    internal fun imagePickerNotAvailableLogs() {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "Image Picker is not available")
        }
    }
}