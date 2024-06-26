package com.example.travelpal.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.travelpal.databinding.FragmentTravelLivetrackingBinding
import com.example.travelpal.repository.TravelRepository
import com.example.travelpal.ui.service.TrackerService
import com.example.travelpal.ui.util.BitmapConverter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch

class TravelLivetrackingFragment : Fragment() {
    private val args: TravelLivetrackingFragmentArgs by navArgs()
    private lateinit var binding: FragmentTravelLivetrackingBinding
    private var trackerService: TrackerService? = null
    private var isBound = false

    private var googleMap: GoogleMap? = null
    private var pathPoints = mutableListOf<LatLng>()

    private val travelRepository: TravelRepository by lazy {
        TravelRepository(requireContext())
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TrackerService.LocalBinder
            trackerService = binder.getService()
            isBound = true

            trackerService?.stepCountData?.observe(viewLifecycleOwner) { stepCount ->
                binding.steps.text = stepCount.toString()
            }

            trackerService?.totalDistanceData?.observe(viewLifecycleOwner) { totalDistance ->
                val text = "%.2f km".format(totalDistance / 1000)
                binding.distance.text = text
            }

            trackerService?.averageSpeedData?.observe(viewLifecycleOwner) { speed ->
                val text = "%.2f km/h".format(speed * 3.6)
                binding.speed.text = text
            }

            trackerService?.lastAltitudeData?.observe(viewLifecycleOwner) { lastAltitude ->
                val text = "%.2f m".format(lastAltitude)
                binding.elevation.text = text
            }

            trackerService?.timeRunInSeconds?.observe(viewLifecycleOwner) { timeInSeconds ->
                val hours = timeInSeconds / 3600
                val minutes = (timeInSeconds % 3600) / 60
                val seconds = timeInSeconds % 60
                binding.stopwatch.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }

            trackerService?.locationsData?.observe(viewLifecycleOwner) { location ->
                pathPoints.add(LatLng(location.latitude, location.longitude))
                addLatestPolyline()
                moveCameraToUser()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            trackerService = null
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTravelLivetrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.travelName.text = args.travelEntity.destinationName
        binding.stopTracking.setOnClickListener {
            Intent(requireActivity().applicationContext, TrackerService::class.java).apply {
                action = TrackerService.ACTION_STOP
                putExtra("travelEntityId", args.travelEntity.id)
                requireActivity().startService(this)
            }
            googleMap?.addMarker(
                MarkerOptions().position(pathPoints.first())
            )
            googleMap?.addMarker(
                MarkerOptions().position(pathPoints.last())
            )
            googleMap?.snapshot { bitmap ->
                val travelEntity = args.travelEntity
                travelEntity.mapThumbnail = BitmapConverter().bitmapToByteArray(bitmap!!)
                lifecycleScope.launch {
                    travelRepository.updateTravel(travelEntity)
                    findNavController().navigate(TravelLivetrackingFragmentDirections.actionTravelLivetrackingFragmentToTravelListFragment())
                }
            }
        }

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { googleMap ->
            this.googleMap = googleMap
            addAllPolylines()
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty()) {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last(),
                    17f
                )
            )
        }
    }

    private fun addAllPolylines() {
        val polylineOptions = PolylineOptions()
            .color(Color.BLUE)
            .width(5f)
            .addAll(pathPoints)
        googleMap?.addPolyline(polylineOptions)
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.size > 1) {
            val preLastPoint = pathPoints[pathPoints.size - 2]
            val lastPoint = pathPoints.last()
            val polylineOptions = PolylineOptions()
                .color(Color.RED)
                .width(5f)
                .add(preLastPoint)
                .add(lastPoint)
            googleMap?.addPolyline(polylineOptions)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        Intent(requireActivity(), TrackerService::class.java).also { intent ->
            requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireActivity().unbindService(serviceConnection)
            isBound = false
        }
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}
