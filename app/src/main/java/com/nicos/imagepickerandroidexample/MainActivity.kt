package com.nicos.imagepickerandroidexample

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nicos.imagepickerandroid.image_picker.ImagePicker
import com.nicos.imagepickerandroid.image_picker.ImagePickerInterface
import com.nicos.imagepickerandroidexample.adapters.ListImagesAdapter
import com.nicos.imagepickerandroidexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ImagePickerInterface {

    private lateinit var binding: ActivityMainBinding
    private var listImageAdapter: ListImagesAdapter? = null
    private var imagePicker: ImagePicker? = null

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
        imagePicker = ImagePicker(
            fragmentActivity = this,
            coroutineScope = lifecycleScope,
            enabledBase64ValueForMultipleImages = true,
            enabledBase64ValueForSingleImage = true,
            imagePickerInterface = this
        )
        imagePicker?.initPickSingleImageFromGalleryResultLauncher()
        imagePicker?.initPickMultipleImagesFromGalleryResultLauncher()
        imagePicker?.initTakePhotoWithCameraResultLauncher()
    }

    private fun initListeners() {
        binding.pickImage.setOnClickListener {
            imagePicker?.pickSingleImageFromGallery()
        }
        binding.pickImages.setOnClickListener {
            imagePicker?.pickMultipleImagesFromGallery()
        }
        binding.camera.setOnClickListener {
            imagePicker?.takeSinglePhotoWithCamera()
        }
    }

    /**
     * Call Back - Get Bitmap and Uri
     * */
    override fun onGallerySingleImageWithBase64Value(
        bitmap: Bitmap?,
        uri: Uri?,
        base64AsString: String?
    ) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        base64AsString?.let { Log.d("base64AsString", it) }
        super.onGallerySingleImageWithBase64Value(bitmap, uri, base64AsString)
    }

    override fun onCameraImage(bitmap: Bitmap?) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        super.onCameraImage(bitmap)
    }

    override fun onMultipleGalleryImagesWithBase64Value(
        bitmapList: MutableList<Bitmap>?,
        uriList: MutableList<Uri>?,
        base64AsStringList: MutableList<String>?
    ) {
        if (bitmapList != null) listImageAdapter?.loadData(bitmapList)
        base64AsStringList?.forEach {
            it.let { Log.d("base64AsString", it) }
        }
        super.onMultipleGalleryImagesWithBase64Value(bitmapList, uriList, base64AsStringList)
    }
}