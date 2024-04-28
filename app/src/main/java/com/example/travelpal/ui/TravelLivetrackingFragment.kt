package com.example.travelpal.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.travelpal.data.TravelEntity
import com.example.travelpal.databinding.FragmentTravelLivetrackingBinding
import com.example.travelpal.repository.TravelRepository
import com.example.travelpal.ui.dialog.TravelInputDialogFragment
import com.example.travelpal.ui.manager.LiveTrackingManager
import com.example.travelpal.ui.manager.PermissionsManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TravelLivetrackingFragment : Fragment() {
    private val args: TravelLivetrackingFragmentArgs by navArgs()
    private lateinit var binding: FragmentTravelLivetrackingBinding
    private lateinit var liveTrackingManager: LiveTrackingManager
    private val backgroundTaskScope = CoroutineScope(Dispatchers.Default)
    private var trackingJob: Job? = null

    private lateinit var googleMap: GoogleMap

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            // Update your UI here
            //TODO
            binding.speed.text = liveTrackingManager.getAverageSpeed().toString()
            binding.distance.text = liveTrackingManager.getTotalDistance().toString()
            binding.elevation.text = liveTrackingManager.getLastAltitude().toString()
            binding.steps.text = stepCount.toString()
            handler.postDelayed(this, 5000) // Run this every 5 seconds
        }
    }

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private lateinit var sensorEventListener: SensorEventListener
    private var stepCount = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTravelLivetrackingBinding.inflate(inflater, container, false)
        liveTrackingManager = LiveTrackingManager(requireContext(), args.travelEntity)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permissionsManager = PermissionsManager(this)
        binding.stopTracking.setOnClickListener {
            stopLiveTracking()
            findNavController().navigate(TravelLivetrackingFragmentDirections.actionTravelLivetrackingFragmentToTravelListFragment())
        }
//        binding.mapView.onCreate(savedInstanceState)
//        binding.mapView.getMapAsync(this)

        startLiveTracking(args.travelEntity.id)
        handler.post(runnable)
    }

    private fun startLiveTracking(travelEntityId: Long) {
        this.trackingJob = lifecycleScope.launch {
            sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            sensorEventListener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // You can implement this method if you want to react to changes in the sensor's accuracy
                }

                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor == stepCounterSensor) {
                        stepCount = event.values[0].toInt()
                        // Do something with the step count
                        // For example, you can save it to your database or display it in your UI
                    }
                }
            }
            sensorManager.registerListener(sensorEventListener, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)

            withContext(Dispatchers.IO) {
                liveTrackingManager.startLocationUpdates(travelEntityId)
//                if (liveTrackingManager.getLastLocation() != null) {
//                    val currentLocation = LatLng(liveTrackingManager.getLastLocation()!!.latitude, liveTrackingManager.getLastLocation()!!.longitude)
//                    googleMap.clear()
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
//                    googleMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
//                }
            }
        }
    }

//    override fun onMapReady(map: GoogleMap) {
//        googleMap = map
//        // Add a marker for the current location and move the camera
//        if (liveTrackingManager.getLastLocation() != null) {
//            val currentLocation = LatLng(liveTrackingManager.getLastLocation()!!.latitude, liveTrackingManager.getLastLocation()!!.longitude)
//            googleMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
//        }
//        startLiveTracking(args.travelEntity.id)
//    }


    private fun stopLiveTracking() {
        trackingJob?.cancel()
        sensorManager.unregisterListener(sensorEventListener)
        liveTrackingManager.stopLocationUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        trackingJob?.cancel()
    }



}
