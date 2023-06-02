package com.nicos.imagepickerandroid.utils.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.nicos.imagepickerandroid.image_picker.ImagePicker

class PermissionsHelper(
    private var imagePicker: ImagePicker
) {

    internal var activityResultLauncherPermissionFragment: ActivityResultLauncher<String>? = null
    internal var activityResultLauncherPermissionActivity: ActivityResultLauncher<String>? = null

    internal fun isPermissionGranted(fragmentActivity: FragmentActivity) =
        (ActivityCompat.checkSelfPermission(
            fragmentActivity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)


    internal fun isPermissionGranted(fragment: Fragment) =
        (fragment.context?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)

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