package com.example.travelpal.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTravelLivetrackingBinding.inflate(inflater, container, false)
        liveTrackingManager = LiveTrackingManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val permissionsManager = PermissionsManager(this)
        binding.stopTracking.setOnClickListener {
            stopLiveTracking()
            findNavController().navigate(TravelLivetrackingFragmentDirections.actionTravelLivetrackingFragmentToTravelListFragment())
        }

        startLiveTracking(args.travelEntity.id)

    }

    private fun startLiveTracking(travelEntityId: Long) {
        this.trackingJob = lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                liveTrackingManager.startLocationUpdates(travelEntityId)
            }
        }
    }


    private fun stopLiveTracking() {
        trackingJob?.cancel()
        liveTrackingManager.stopLocationUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        trackingJob?.cancel()
    }



}
