package com.example.travelpal.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.travelpal.R
import com.example.travelpal.data.TravelEntity
import com.example.travelpal.databinding.ItemTripBinding
import com.example.travelpal.ui.util.BitmapConverter

class TravelAdapter(
    private val onClick: (TravelEntity) -> Unit
) : ListAdapter<TravelEntity, TravelViewHolder>(TravelDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelViewHolder =
        TravelViewHolder(
            ItemTripBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TravelViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onClick)
    }
}


class TravelViewHolder(
    private val binding: ItemTripBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: TravelEntity, onClick: (TravelEntity) -> Unit) {
        if (item.mapThumbnail != null) {
            binding.ivTripThumbnail.load(BitmapConverter().byteArrayToBitmap(item.mapThumbnail!!)) {
                crossfade(true)
                transformations(RoundedCornersTransformation(16f))
            }
        } else {
            binding.ivTripThumbnail.load(R.drawable.baseline_image_24) {
                crossfade(true)
                transformations(RoundedCornersTransformation(16f))
            }
        }

        binding.tvDescription.text = item.description
        binding.tvTripDate.text = item.date
        binding.tvDestinationName.text = item.destinationName

        binding.root.setOnClickListener {
            onClick(item)
        }
    }


}

class TravelDiffUtil : DiffUtil.ItemCallback<TravelEntity>() {
    override fun areItemsTheSame(oldItem: TravelEntity, newItem: TravelEntity): Boolean =
        oldItem.id == newItem.id


    override fun areContentsTheSame(oldItem: TravelEntity, newItem: TravelEntity): Boolean =
        oldItem == newItem
}