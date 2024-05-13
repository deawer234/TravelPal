package com.example.travelpal.ui

import android.Manifest
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
import com.example.travelpal.ui.util.Chart
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.travelpal.R
import com.example.travelpal.data.Photo
import com.example.travelpal.databinding.DialogUpdateBinding
import com.example.travelpal.repository.TravelRepository
import com.example.travelpal.ui.adapter.ImagesAdapter
import com.example.travelpal.ui.util.BitmapConverter

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TravelDetailFragment : Fragment(), OnMapReadyCallback {
    private val args: TravelDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentTravelDetailBinding

    private lateinit var locations: List<Location>

    private lateinit var toolbar: Toolbar

    private val imagesAdapter: ImagesAdapter = ImagesAdapter(
        onDelete = { photo ->
            lifecycleScope.launch {
                photoRepository.deletePhoto(photo)
            }
        }
    )

    private val photoRepository: PhotoRepository by lazy {
        PhotoRepository(requireContext())
    }
    private val locationRepository: LocationRepository by lazy {
        LocationRepository(requireContext())
    }

    private val travelRepository: TravelRepository by lazy {
        TravelRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTravelDetailBinding.inflate(inflater, container, false)
        return binding.root

    }


    private val requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            when {
                results[Manifest.permission.READ_MEDIA_IMAGES] == true && results[Manifest.permission.READ_MEDIA_VIDEO] == true && results[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true -> {
                    pickImagesLauncher.launch("image/*")
                }
                else -> {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                results[Manifest.permission.READ_MEDIA_IMAGES] == true && results[Manifest.permission.READ_MEDIA_VIDEO] == true -> {
                    pickImagesLauncher.launch("image/*")
                }
                else -> {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            when {
                results[Manifest.permission.READ_EXTERNAL_STORAGE] == true -> {
                    pickImagesLauncher.launch("image/*")
                }
                else -> {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.menu.clear()
        toolbar.inflateMenu(R.menu.overflow_menu)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    pickThumbnailLauncher.launch("image/*")
                    true
                }
                R.id.change_title -> {
                    updateTitle()
                    true
                }
                R.id.change_desc -> {
                    updateDescription()
                    true
                }
                else -> false
            }
        }

        locations = locationRepository.getAllTravelLocations(args.travelEntity.id)
        binding.photosRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.photosRecyclerView.adapter = imagesAdapter

        binding.addImg.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requestPermissions.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
            } else {
                requestPermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
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
        val totalTime = (locations.last().visitDate.toLong() - locations.first().visitDate.toLong())
        val steps = "Steps: ${locations.last().steps}"
        val hours = totalTime / 3600
        val minutes = (totalTime % 3600) / 60
        val seconds = totalTime % 60
        val timeText = "Total time: $hours Hours $minutes Minutes $seconds Seconds"
        binding.totalTime.text = timeText
        binding.averageSpeed.text = averageSpeedText
        binding.distanceTraveled.text = distanceTraveledText
        binding.steps.text = steps


        val chart = Chart()
        lifecycleScope.launch {
            chart.getElevationChartData(binding.elevationChart, locations)
        }
    }

    private fun showUpdateDialog(currentText: String, onUpdate: (String) -> Unit) {
        val dialogBinding = DialogUpdateBinding.inflate(LayoutInflater.from(context))
        dialogBinding.editText.setText(currentText)

        AlertDialog.Builder(requireContext())
            .setTitle("Update Text")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val newText = dialogBinding.editText.text.toString()
                onUpdate(newText)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateTitle() {
        showUpdateDialog(args.travelEntity.destinationName) { newTitle ->
            lifecycleScope.launch {
                val updatedTravelEntity = args.travelEntity.copy(destinationName = newTitle)
                travelRepository.updateTravel(updatedTravelEntity)
            }
            binding.tvDestinationName.text = newTitle
        }
    }

    private fun updateDescription() {
        showUpdateDialog(args.travelEntity.description) { newDescription ->
            lifecycleScope.launch {
                val updatedTravelEntity = args.travelEntity.copy(description = newDescription)
                travelRepository.updateTravel(updatedTravelEntity)
            }
            binding.tvDescription.text = newDescription
        }
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

    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        val photosToInsert = mutableListOf<Photo>()
        uris.forEach { uri ->
            val photo = Photo(
                travelEntryId = args.travelEntity.id,
                uri = uri.toString(),
                description = "",
                dateTaken = System.currentTimeMillis()
            )
            photosToInsert.add(photo)
        }
        lifecycleScope.launch {
            photoRepository.insertPhotos(photosToInsert)
        }
    }

    private val pickThumbnailLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                try {
                    val bitmap = BitmapConverter().decodeUriToBitmap(uri, requireContext())
                    val byteArray = BitmapConverter().bitmapToByteArray(bitmap)
                    args.travelEntity.mapThumbnail = byteArray
                    travelRepository.updateTravel(args.travelEntity)
                } catch (e: Exception) {
                    Log.e("setThumbnail", "Error processing image", e)
                }
            }
        }
    }



    override fun onStart() {
        super.onStart()
        binding.ivMapThumbnail.onStart()
    }


    override fun onResume() {
        super.onResume()
        imagesAdapter.submitList(photoRepository.getAllPhotosForTravel(args.travelEntity.id))
        binding.ivMapThumbnail.onResume()
    }

    override fun onStop() {
        super.onStop()
        toolbar.menu.clear()
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