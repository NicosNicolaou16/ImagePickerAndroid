package com.nick.imagepickerandroidexample

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nick.imagepickerandroidexample.adapters.ListImagesAdapter
import com.nicos.imagepickerandroid.image_picker.ImagePicker.initPickMultipleImagesFromGalleryResultLauncher
import com.nicos.imagepickerandroid.image_picker.ImagePicker.initPickSingleImageFromGalleryResultLauncher
import com.nicos.imagepickerandroid.image_picker.ImagePicker.initTakePhotoWithCameraResultLauncher
import com.nicos.imagepickerandroid.image_picker.ImagePicker.pickMultipleImagesFromGallery
import com.nicos.imagepickerandroid.image_picker.ImagePicker.pickSingleImageFromGallery
import com.nicos.imagepickerandroid.image_picker.ImagePicker.takeSinglePhotoWithCamera
import com.nicos.imagepickerandroid.image_picker.ImagePickerInterface
import com.nicos.imagepickerandroidexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ImagePickerInterface {

    private lateinit var binding: ActivityMainBinding
    private var listImageAdapter: ListImagesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initImagePicker()
        initAdapter()
        initListeners()
    }

    private fun initAdapter() {
        listImageAdapter = ListImagesAdapter(mutableListOf())
        binding.recyclerView.adapter = listImageAdapter
    }

    private fun initImagePicker() {
        initPickSingleImageFromGalleryResultLauncher(
            fragmentActivity = this,
            coroutineScope = lifecycleScope,
            enabledBase64 = true, //optional
            imagePickerInterface = this,
        )
        initPickMultipleImagesFromGalleryResultLauncher(
            fragmentActivity = this,
            coroutineScope = lifecycleScope,
            enabledBase64 = true, //optional
            imagePickerInterface = this,
        )
        initTakePhotoWithCameraResultLauncher(
            fragmentActivity = this,
            coroutineScope = lifecycleScope,
            enabledBase64 = true, //optional
            imagePickerInterface = this,
        )
    }

    private fun initListeners() {
        binding.pickImage.setOnClickListener {
            pickSingleImageFromGallery(fragmentActivity = this)
        }
        binding.pickImages.setOnClickListener {
            pickMultipleImagesFromGallery(fragmentActivity = this)
        }
        binding.camera.setOnClickListener {
            takeSinglePhotoWithCamera(fragmentActivity = this)
        }
    }

    /**
     * Call Back - Get Bitmap and Uri
     * */
    override fun onGalleryImage(bitmap: Bitmap?, uri: Uri?, base64AsString: String?) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        base64AsString?.let { Log.d("base64AsString", it) }
        super.onGalleryImage(bitmap, uri, base64AsString)
    }

    override fun onCameraImage(bitmap: Bitmap?, base64AsString: String?) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        base64AsString?.let { Log.d("base64AsString", it) }
        super.onCameraImage(bitmap, base64AsString)
    }

    override fun onMultipleGalleryImages(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?,
        base64AsStringList: MutableList<String>?
    ) {
        if (bitmapList != null) listImageAdapter?.loadData(bitmapList)
        base64AsStringList?.forEach {
            it.let { Log.d("base64AsString", it) }
        }
        super.onMultipleGalleryImages(bitmapList, uriList, base64AsStringList)
    }
}