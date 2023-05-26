package com.nick.imagepickerandroidexample

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nick.imagepickerandroidexample.adapters.ListImagesAdapter
import com.nicos.imagepickerandroid.image_picker.ImagePicker.initPickSingleImageFromGalleryResultLauncher
import com.nicos.imagepickerandroid.image_picker.ImagePicker.initPickMultipleImagesFromGalleryResultLauncher
import com.nicos.imagepickerandroid.image_picker.ImagePicker.initTakePhotoWithCameraResultLauncher
import com.nicos.imagepickerandroid.image_picker.ImagePicker.pickSingleImageFromGallery
import com.nicos.imagepickerandroid.image_picker.ImagePicker.pickMultipleImagesFromGallery
import com.nicos.imagepickerandroid.image_picker.ImagePicker.takeAPhotoWithCamera
import com.nicos.imagepickerandroid.image_picker.ImagePickerInterface
import com.nicos.imagepickerandroidexample.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            imagePickerInterface = this
        )
        initPickMultipleImagesFromGalleryResultLauncher(
            fragmentActivity = this,
            coroutineScope = lifecycleScope,
            imagePickerInterface = this
        )
        initTakePhotoWithCameraResultLauncher(
            fragmentActivity = this,
            imagePickerInterface = this
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
            takeAPhotoWithCamera(fragmentActivity = this)
        }
    }

    /**
     * Call Back - Get Bitmap and Uri
     * */
    override fun onGalleryImage(bitmap: Bitmap?, uri: Uri?) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        super.onGalleryImage(bitmap, uri)
    }

    override fun onCameraImage(bitmap: Bitmap?, uri: Uri?) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        super.onCameraImage(bitmap, uri)
    }

    override fun onMultipleGalleryImages(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (bitmapList != null) listImageAdapter?.loadData(bitmapList)
        }
        super.onMultipleGalleryImages(bitmapList, uriList)
    }
}