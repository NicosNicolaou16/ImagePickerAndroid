package com.nicos.imagepickerandroid.utils.image_helper_methods

import androidx.annotation.IntRange

data class ScaleBitmapModel(
    @IntRange(from = 1, to = 1000) var height: Int,
    @IntRange(from = 1, to = 1000)  var width: Int
)
