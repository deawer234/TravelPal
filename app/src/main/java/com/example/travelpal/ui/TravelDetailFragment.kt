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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TravelDetailFragment : Fragment(), OnMapReadyCallback {
    private val args: TravelDetailFragmentArgs by navArgs()

    private lateinit var binding: FragmentTravelDetailBinding

    private lateinit var locations: List<Location>

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

        val photos = photoRepository.getAllPhotosForTravel(args.travelEntity.id)
        binding.photosRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = PhotoAdapter(requireContext(), photos)
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
        binding.totalTime.text = "Total time: ${totalTime}"
        val chart = Chart()
        lifecycleScope.launch {
            chart.getElevationChartData(binding.elevationChart, locations)
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