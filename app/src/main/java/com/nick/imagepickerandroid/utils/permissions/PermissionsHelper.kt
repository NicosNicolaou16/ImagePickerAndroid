package com.nick.imagepickerandroid.utils.permissions

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.nick.imagepickerandroid.image_picker.ImagePicker

open class PermissionsHelper {

    protected var activityResultLauncherPermissionFragment: ActivityResultLauncher<String>? = null
    protected var activityResultLauncherPermissionActivity: ActivityResultLauncher<String>? = null

    protected fun isPermissionGranted(fragmentActivity: FragmentActivity) =
        (ActivityCompat.checkSelfPermission(
            fragmentActivity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED)


    protected fun isPermissionGranted(fragment: Fragment) =
        (fragment.context?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)

    protected fun initRegisterForRequestPermissionInFragment(
        fragment: Fragment?,
    ) {
        activityResultLauncherPermissionFragment = fragment?.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) ImagePicker.takeAPhotoWithCamera(fragment = fragment)
        }
    }

    protected fun initRegisterForRequestPermissionInActivity(
        fragmentActivity: FragmentActivity?,
    ) {
        activityResultLauncherPermissionActivity = fragmentActivity?.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) ImagePicker.takeAPhotoWithCamera(fragmentActivity = fragmentActivity)
        }
    }
}