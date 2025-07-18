package com.nicos.imagepickerandroid.utils.extensions

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

private const val authority = ".library.file.provider"

internal fun File.getUriWithFileProvider(context: Context): Uri {
    require(!this.exists()) { "File must exist to get a URI with FileProvider" }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}$authority",
        this // 'this' refers to the File instance the extension is called on
    )
}