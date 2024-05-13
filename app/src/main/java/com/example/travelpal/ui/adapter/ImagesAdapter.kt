package com.example.travelpal.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
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

class ImagesAdapter(private val onDelete: (Photo) -> Unit) : ListAdapter<Photo, ImageViewHolder>(ImageDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onDelete
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val photo = getItem(position)
        holder.bind(photo)
    }
}

class ImageViewHolder(private val binding: ImageItemBinding, private val onDelete: (Photo) -> Unit) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Photo) {
        binding.imageView.load(item.uri)
        binding.imageView.setOnLongClickListener {
            AlertDialog.Builder(it.context)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete") { _, _ ->
                    onDelete(item)
                }
                .setNegativeButton("Cancel", null)
                .show()

            true
        }
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