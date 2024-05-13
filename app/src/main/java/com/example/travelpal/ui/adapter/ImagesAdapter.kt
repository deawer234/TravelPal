package com.example.travelpal.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.example.travelpal.R
import com.example.travelpal.data.Photo
import com.example.travelpal.data.TravelEntity
import com.example.travelpal.databinding.ImageItemBinding
import com.example.travelpal.databinding.ItemTripBinding

class ImagesAdapter : ListAdapter<Photo, ImageViewHolder>(ImageDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)

        return ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
//        val uri = Uri.parse(images[position])
//        Glide.with(holder.itemView.context).load(uri).into(holder.imageView)
        val photo = getItem(position)
        holder.bind(photo)
    }
}

class ImageViewHolder(private val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Photo) {
        binding.imageView.load(item.uri)
    }
}


class ImageDiffUtil : DiffUtil.ItemCallback<Photo>(){
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }

}