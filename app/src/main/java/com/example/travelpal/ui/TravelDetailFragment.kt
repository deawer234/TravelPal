package com.example.travelpal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelpal.data.Photo
import com.example.travelpal.databinding.FragmentTravelDetailBinding
import com.example.travelpal.ui.adapter.PhotoAdapter

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TravelDetailFragment : Fragment() {
    private val args: TravelDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentTravelDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTravelDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photos = listOf(
            Photo(uri = "file:///android_asset/image1.jpg", description = "Image 1", travelEntryId =  args.TravelEntity.id, dateTaken = System.currentTimeMillis()),
            Photo(uri = "file:///android_asset/image2.jpg", description = "Image 2", travelEntryId =  args.TravelEntity.id, dateTaken = System.currentTimeMillis())
            // Add more photos
        )

        binding.photosRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = PhotoAdapter(requireContext(), photos)
        }

        binding.tvDescription.text = args.TravelEntity.description
        binding.tvDestinationName.text = args.TravelEntity.destinationName
        //binding.ivMapThumbnail.
    }


}