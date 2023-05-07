package com.nick.imagepickerandroid

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.nick.imagepickerandroid.adapters.ListImagesAdapter
import com.nick.imagepickerandroid.databinding.ActivityMainBinding
import com.nick.imagepickerandroid.image_picker.ImagePicker.initPickAPhotoFromGalleryResultLauncher
import com.nick.imagepickerandroid.image_picker.ImagePicker.initPickMultiplePhotoFromGalleryResultLauncher
import com.nick.imagepickerandroid.image_picker.ImagePicker.pickAnImageFromGallery
import com.nick.imagepickerandroid.image_picker.ImagePicker.pickMultipleImagesFromGallery
import com.nick.imagepickerandroid.image_picker.ImagePickerInterface
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
        initPickAPhotoFromGalleryResultLauncher(
            fragmentActivity = this,
            fileAndImageTakePickerInterface = this
        )
        initPickMultiplePhotoFromGalleryResultLauncher(
            fragmentActivity = this,
            coroutineScope = lifecycleScope,
            imagePickerInterface = this
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

    /**
     * Call Back - Get Bitmap and Uri
     * */
    override fun onBitmap(bitmap: Bitmap?, uri: Uri?) {
        if (bitmap != null) binding.image.setImageBitmap(bitmap)
        super.onBitmap(bitmap, uri)
    }

    override fun onMultipleBitmaps(bitmapList: MutableList<Bitmap>?, uriList: MutableList<Uri>?) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (bitmapList != null) listImageAdapter?.loadData(bitmapList)
        }
        super.onMultipleBitmaps(bitmapList, uriList)
    }
}