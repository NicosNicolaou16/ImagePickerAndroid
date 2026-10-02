package com.nicos.imagepickerandroid.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

private const val authority = ".library.file.provider"

internal fun File.getUriWithFileProvider(context: Context): Uri {
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}$authority",
        this // 'this' refers to the File instance the extension is called on
    )
}

/**
 * Finds the [Activity] that hosts this [Context].
 *
 * In Compose, `LocalContext.current` is not always the Activity itself. It is often wrapped
 * in one or more [ContextWrapper]s (for example a `ContextThemeWrapper` from a theme, or
 * Hilt's `FragmentContextWrapper`). Casting it directly with `context as Activity` throws a
 * [ClassCastException] in those cases.
 *
 * This function walks up the wrapper chain via [ContextWrapper.getBaseContext] until it
 * reaches an Activity:
 * - If the current context is an [Activity], it is returned.
 * - If it is a [ContextWrapper], the search continues with its `baseContext`.
 * - Otherwise (for example `Application` or the system's base context), there is nothing
 *   left to unwrap and `null` is returned.
 *
 * The [Activity] check must come before the [ContextWrapper] check, because [Activity]
 * itself extends [ContextWrapper]; swapping them would skip past the Activity.
 *
 * Marked `tailrec`, so the compiler turns the recursion into a loop: there is no risk of a
 * stack overflow, however many wrapper layers there are.
 *
 * Example:
 * ```
 * val activity = context.findActivity()
 * if (activity != null) {
 *     shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
 * }
 * ```
 *
 * @receiver the context to search from, e.g. `LocalContext.current`
 * @return the hosting [Activity], or `null` if this context is not attached to one
 * */
internal tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}