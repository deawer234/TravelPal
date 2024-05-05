package com.example.travelpal.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelpal.data.TravelEntity
import com.example.travelpal.databinding.FragmentTravelCreateBinding
import com.example.travelpal.repository.TravelRepository
import com.example.travelpal.ui.service.TrackerService


class TravelCreateFragment : Fragment(){
    private lateinit var binding: FragmentTravelCreateBinding

    private val travelRepository: TravelRepository by lazy {
        TravelRepository(requireContext())
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted.
        }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTravelCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

//    private val requestPermission =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                saveTravelEntity()
//            } else {
//                Snackbar.make(
//                    binding.root,
//                    "Permissions are required to create a travel",
//                    Snackbar.LENGTH_SHORT
//                ).show()
//            }
//        }

//    private var activityResultLauncher: ActivityResultLauncher<Array<String>>
//    init {
//        this.activityResultLauncher = registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { result ->
//            var allAreGranted = true
//            for (b in result.values) {
//                allAreGranted = allAreGranted && b
//            }
//
//            if (allAreGranted) {
//                saveTravelEntity()
//            } else {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS),
//                    1)
//            }
//        }
//    }

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            saveTravelEntity()
        }
        else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //permissionsManager.checkAndRequestPermissions()
//        if(ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) {
//
//        }

        binding.btnSave.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                -> {
                    saveTravelEntity()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        && ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
        }
    }


    private fun saveTravelEntity() {
        val destinationName = binding.etDestinationName.text.toString()
        val date = binding.etDate.text.toString()
        val description = binding.etDescription.text.toString()

        if (destinationName.isNotEmpty() && date.isNotEmpty() && description.isNotEmpty()) {
            //Temporary measure
            val travelEntity = TravelEntity(
                destinationName = destinationName,
                date = date,
                description = description,
                coverUrl = null
            )
            val id = travelRepository.createTravel(travelEntity)
            val travelEntityCreated = travelRepository.getTravelById(id)

            println("BEFORE TRACKING")
            Intent(requireActivity().applicationContext, TrackerService::class.java).apply {
                action = TrackerService.ACTION_START
                putExtra("travelEntityId", id)
                requireActivity().startService(this)
            }
            println("STARTED TRACKING")

            findNavController().navigate(TravelCreateFragmentDirections.actionCreateTravelFragmentToTravelLivetrackingFragment(travelEntityCreated))
            // Navigate back or show a success message
        } else {
            // Show an error message indicating that all fields are required
        }
    }

}