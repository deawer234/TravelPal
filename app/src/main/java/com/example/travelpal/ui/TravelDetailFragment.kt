package com.example.travelpal.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelpal.data.Location
import com.example.travelpal.databinding.FragmentTravelDetailBinding
import com.example.travelpal.repository.LocationRepository
import com.example.travelpal.repository.PhotoRepository
import com.example.travelpal.ui.adapter.PhotoAdapter
import com.example.travelpal.ui.util.Chart
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.travelpal.data.Photo
import com.example.travelpal.ui.adapter.ImagesAdapter

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TravelDetailFragment : Fragment(), OnMapReadyCallback {
    private val args: TravelDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentTravelDetailBinding

    private lateinit var locations: List<Location>

    private lateinit var imagesAdapter: ImagesAdapter

    private var imageList: MutableList<String> = mutableListOf()

    private val photoRepository: PhotoRepository by lazy {
        PhotoRepository(requireContext())
    }
    private val locationRepository: LocationRepository by lazy {
        LocationRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTravelDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locations = locationRepository.getAllTravelLocations(args.travelEntity.id)

//        val photos = photoRepository.getAllPhotosForTravel(args.travelEntity.id)
//        binding.photosRecyclerView.apply {
//            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//            adapter = PhotoAdapter(requireContext(), photos)
//        }

        imagesAdapter = ImagesAdapter(imageList)
        binding.photosRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = imagesAdapter
        }

        binding.addImg.setOnClickListener {
            openGalleryToSelectImages()
        }


        binding.ivMapThumbnail.onCreate(savedInstanceState)
        binding.ivMapThumbnail.getMapAsync(this)

        binding.tvDescription.text = args.travelEntity.description
        binding.tvDestinationName.text = args.travelEntity.destinationName

        var average = 0f
        locations.forEach {
            average += it.speed
        }
        average /= locations.size

        val averageSpeedText = "Average speed: %.2f km/h".format(average*3.6)
        val distanceTraveledText = "Distance traveled: %.2f km".format(locations.last().traveled/1000)
        binding.averageSpeed.text = averageSpeedText
        binding.distanceTraveled.text = distanceTraveledText

        binding.steps.text = "Steps: ${locations.last().steps}"

        val totalTime = (locations.last().visitDate.toLong() - locations.first().visitDate.toLong())/1000
        binding.totalTime.text = "Total time: ${totalTime/360}Hours ${totalTime%360}Minutes ${totalTime%60}Seconds"
        val chart = Chart()
        lifecycleScope.launch {
            chart.getElevationChartData(binding.elevationChart, locations)
        }
        loadPhotos()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val latLngList: List<LatLng> = locations.map { location ->
            LatLng(location.latitude, location.longitude)
        }

        googleMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    locations.first().latitude,
                    locations.first().longitude
                )
            )
        )

        googleMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    locations.last().latitude,
                    locations.last().longitude
                )
            )
        )
        locations.firstOrNull()?.let {
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        it.latitude,
                        it.longitude
                    ), 10f
                )
            )
        }
        val polylineOptions =
            PolylineOptions().addAll(latLngList).color(Color.RED).width(8f)
        googleMap.addPolyline(polylineOptions)

        val boundsBuilder = LatLngBounds.builder()
        locations.forEach { boundsBuilder.include(LatLng(it.latitude, it.longitude)) }
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                boundsBuilder.build(),
                100
            )
        )
    }

    private fun openGalleryToSelectImages() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 101
        private const val REQUEST_PICK_IMAGE = 102
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryToSelectImages()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val clipData = data.clipData
            val photosToInsert = mutableListOf<Photo>()
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    val photo = Photo(
                        travelEntryId = args.travelEntity.id,
                        uri = imageUri.toString(),
                        description = "",
                        dateTaken = System.currentTimeMillis()
                    )
                    photosToInsert.add(photo)
                }
            } else if (data.data != null) {
                val imageUri = data.data!!
                val photo = Photo(
                    travelEntryId = args.travelEntity.id,
                    uri = imageUri.toString(),
                    description = "",
                    dateTaken = System.currentTimeMillis()
                )
                photosToInsert.add(photo)
            }
            photoRepository.insertPhotos(photosToInsert)
            imageList.addAll(photosToInsert.map { it.uri })
            imagesAdapter.notifyDataSetChanged()
        }
    }

    private fun loadPhotos() {
        lifecycleScope.launch {
            val photos = photoRepository.getAllPhotosForTravel(args.travelEntity.id)
            imageList.clear()
            imageList.addAll(photos.map { it.uri })
            imagesAdapter.notifyDataSetChanged()
        }
    }



    override fun onStart() {
        super.onStart()
        binding.ivMapThumbnail.onStart()
    }


    override fun onResume() {
        super.onResume()
        binding.ivMapThumbnail.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.ivMapThumbnail.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.ivMapThumbnail.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.ivMapThumbnail.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.ivMapThumbnail.onSaveInstanceState(outState)
    }
}