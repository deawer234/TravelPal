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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelpal.R
import com.example.travelpal.data.TravelEntity
import com.example.travelpal.databinding.FragmentTravelCreateBinding
import com.example.travelpal.databinding.FragmentTravelLivetrackingBinding
import com.example.travelpal.repository.TravelRepository
import com.example.travelpal.ui.dialog.TravelInputDialogFragment
import com.example.travelpal.ui.manager.LiveTrackingManager
import java.time.LocalDate

class TravelLivetrackingFragment : Fragment(), TravelInputDialogFragment.InputListener {
    private lateinit var binding: FragmentTravelLivetrackingBinding
    private lateinit var liveTrackingManager: LiveTrackingManager

    private val travelRepository: TravelRepository by lazy {
        TravelRepository(requireContext())
    }

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

        TravelInputDialogFragment.newInstance(this).show(parentFragmentManager, "TravelInputDialogFragment")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onInputComplete(destinationName: String, description: String) {
        var travelEntity = TravelEntity(
            destinationName = destinationName,
            date = LocalDate.now().toString(),
            description = description,
            coverUrl = null
        )
        var id = travelRepository.createTravel(travelEntity)
        liveTrackingManager.startLocationUpdates(id)

        binding.stopTracking.setOnClickListener {
            stopLiveTracking()
            findNavController().navigate(TravelLivetrackingFragmentDirections.actionTravelLivetrackingFragmentToTravelListFragment())
        }
    }


    private fun stopLiveTracking() {
        liveTrackingManager.stopLocationUpdates()
    }


}
