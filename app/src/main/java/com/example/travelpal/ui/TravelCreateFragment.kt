package com.example.travelpal.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelpal.data.TravelEntity
import com.example.travelpal.databinding.FragmentTravelCreateBinding
import com.example.travelpal.repository.TravelRepository
import com.example.travelpal.ui.manager.PermissionsManager
import com.google.android.material.snackbar.Snackbar


class TravelCreateFragment : Fragment(){
    private lateinit var binding: FragmentTravelCreateBinding

    private val travelRepository: TravelRepository by lazy {
        TravelRepository(requireContext())
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSave.setOnClickListener {
//            val perms = arrayOf(
//                Manifest.permission.CAMERA,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.POST_NOTIFICATIONS
//            )
//            activityResultLauncher.launch(perms)
            saveTravelEntity()
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
            findNavController().navigate(TravelCreateFragmentDirections.actionCreateTravelFragmentToTravelLivetrackingFragment(travelEntityCreated))
            // Navigate back or show a success message
        } else {
            // Show an error message indicating that all fields are required
        }
    }

}