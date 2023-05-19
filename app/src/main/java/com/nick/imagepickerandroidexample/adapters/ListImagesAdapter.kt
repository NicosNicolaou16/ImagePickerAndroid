package com.nick.imagepickerandroidexample.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nicos.imagepickerandroidexample.databinding.ImageAdapterLayoutBinding

class ListImagesAdapter(private var imageList: MutableList<Bitmap>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImagesViewHolder(
            ImageAdapterLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun loadData(imageList: MutableList<Bitmap>) {
        this.imageList.clear()
        this.imageList.addAll(imageList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImagesViewHolder -> holder.bindData(imageList[position])
        }
    }

    inner class ImagesViewHolder(var binding: ImageAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(bitmap: Bitmap?) {
            if (bitmap != null) {
                binding.image.setImageBitmap(bitmap)
            }
        }
    }
}