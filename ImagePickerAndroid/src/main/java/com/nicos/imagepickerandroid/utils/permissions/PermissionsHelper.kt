package com.nicos.imagepickerandroid.utils.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.nicos.imagepickerandroid.image_picker.ImagePicker
import com.nicos.imagepickerandroid.utils.extensions.isCameraPermissionDeclared

internal class PermissionsHelper(
    private var imagePicker: ImagePicker
) {

    internal var activityResultLauncherPermissionFragment: ActivityResultLauncher<String>? = null
    internal var activityResultLauncherPermissionActivity: ActivityResultLauncher<String>? = null

    internal fun isPermissionGranted(fragmentActivity: FragmentActivity) =
        !fragmentActivity.isCameraPermissionDeclared() ||
                ActivityCompat.checkSelfPermission(
                    fragmentActivity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED


    internal fun isPermissionGranted(fragment: Fragment): Boolean {
        val context = fragment.context ?: return false
        return !context.isCameraPermissionDeclared() ||
                context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    internal fun initRegisterForRequestPermissionInFragment(
        fragment: Fragment?,
    ) {
        activityResultLauncherPermissionFragment = fragment?.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) imagePicker.takeSinglePhotoWithCamera()
        }
    }

    internal fun initRegisterForRequestPermissionInActivity(
        fragmentActivity: FragmentActivity?,
    ) {
        activityResultLauncherPermissionActivity = fragmentActivity?.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) imagePicker.takeSinglePhotoWithCamera()
        }
    }
}