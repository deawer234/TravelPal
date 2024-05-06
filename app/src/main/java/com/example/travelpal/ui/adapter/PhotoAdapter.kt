package com.example.travelpal.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelpal.data.Photo

class PhotoAdapter(private val context: Context, private val photos: List<Photo>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setPadding(8, 8, 8, 8)
        }
        return PhotoViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Glide.with(context).load(Uri.parse(photos[position].uri)).into(holder.imageView)
    }

    override fun getItemCount() = photos.size
}