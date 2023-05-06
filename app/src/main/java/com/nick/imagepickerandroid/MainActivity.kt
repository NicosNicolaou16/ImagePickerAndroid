package com.nick.imagepickerandroid

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.nick.imagepickerandroid.adapters.ListImages
import com.nick.imagepickerandroid.databinding.ActivityMainBinding
import com.nick.imagepickerandroid.image_picker.ImagePicker.initPickAPhotoFromGalleryResultLauncher
import com.nick.imagepickerandroid.image_picker.ImagePicker.initPickMultiplePhotoFromGalleryResultLauncher
import com.nick.imagepickerandroid.image_picker.ImagePicker.pickAnImageFromGallery
import com.nick.imagepickerandroid.image_picker.ImagePicker.pickMultipleImagesFromGallery
import com.nick.imagepickerandroid.image_picker.ImagePickerInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), ImagePickerInterface {

    private lateinit var binding: ActivityMainBinding
    private var listImage: ListImages? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initImagePicker()
        initAdapter()
        initListeners()
    }

    private fun initAdapter() {
        listImage = ListImages(mutableListOf())
        binding.recyclerView.adapter = listImage
    }

    private fun initImagePicker() {
        initPickAPhotoFromGalleryResultLauncher(
            fragmentActivity = this,
            fileAndImageTakePickerInterface = this
        )
        initPickMultiplePhotoFromGalleryResultLauncher(
            fragmentActivity = this,
            coroutineScope = lifecycleScope,
            fileAndImageTakePickerInterface = this
        )
    }

    private fun initListeners() {
        binding.pickImage.setOnClickListener {
            pickAnImageFromGallery(fragmentActivity = this)
        }
        binding.pickImages.setOnClickListener {
            pickMultipleImagesFromGallery(fragmentActivity = this)
        }
    }

    override fun onBitmap(bitmap: Bitmap?, uri: Uri?) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        super.onBitmap(bitmap, uri)
    }

    override fun onMultipleBitmaps(bitmapList: MutableList<Bitmap>?, uriList: MutableList<Uri>?) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (bitmapList != null) listImage?.loadData(bitmapList)
        }
        super.onMultipleBitmaps(bitmapList, uriList)
    }
}