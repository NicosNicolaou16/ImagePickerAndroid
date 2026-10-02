package com.nicos.imagepickerandroid.model

import android.graphics.Bitmap
import android.net.Uri

/**
 * Result of decoding several Uris.
 * [uris] and [bitmaps] always have the same size, and bitmaps[i] was decoded from uris[i].
 * */
internal data class DecodedImages(
    val uris: MutableList<Uri>,
    val bitmaps: MutableList<Bitmap>,
)