package com.nicos.imagepickerandroid.utils.image_helper_methods

import androidx.annotation.IntRange

/**
 * Target size for resizing images.
 *
 * @param height target height in pixels
 * @param width target width in pixels
 * @param keepAspectRatio
 * - `false` (default): the image is resized to exactly [width] × [height]. If the image has a
 *   different shape, it is stretched.
 * - `true`: the image is resized to fit inside [width] × [height], keeping its shape. The result
 *   can be smaller than the box in one direction (for example 500 × 375 for a 4:3 photo in a
 *   500 × 500 box). Images that are already smaller than the box are not enlarged.
 * */
data class ScaleBitmapModel(
    @IntRange(from = 1, to = 8192) var height: Int,
    @IntRange(from = 1, to = 8192) var width: Int,
    var keepAspectRatio: Boolean = false,
)
