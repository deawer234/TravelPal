package com.example.travelpal.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.travelpal.databinding.FragmentTravelLivetrackingBinding
import com.example.travelpal.ui.service.TrackerService

class TravelLivetrackingFragment : Fragment() {
    private val args: TravelLivetrackingFragmentArgs by navArgs()
    private lateinit var binding: FragmentTravelLivetrackingBinding
    private var trackerService: TrackerService? = null
    private var isBound = false

    //private lateinit var googleMap: GoogleMap


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TrackerService.LocalBinder
            trackerService = binder.getService()
            isBound = true

            trackerService?.stepCountData?.observe(viewLifecycleOwner) { stepCount ->
                binding.steps.text = stepCount.toString()
            }

            trackerService?.totalDistanceData?.observe(viewLifecycleOwner) { totalDistance ->
                binding.distance.text = totalDistance.toString()
            }

            trackerService?.averageSpeedData?.observe(viewLifecycleOwner) { averageSpeed ->
                binding.speed.text = averageSpeed.toString()
            }

            trackerService?.lastAltitudeData?.observe(viewLifecycleOwner) { lastAltitude ->
                binding.elevation.text = lastAltitude.toString()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            trackerService = null
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(requireActivity(), TrackerService::class.java).also { intent ->
            requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireActivity().unbindService(serviceConnection)
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

        binding.stopTracking.setOnClickListener {
            Intent(requireActivity().applicationContext, TrackerService::class.java).apply {
                action = TrackerService.ACTION_STOP
                putExtra("travelEntityId", args.travelEntity.id)
                requireActivity().startService(this)
            }
            print("STOPPED TRACKING")
            findNavController().navigate(TravelLivetrackingFragmentDirections.actionTravelLivetrackingFragmentToTravelListFragment())
        }
    }
}
