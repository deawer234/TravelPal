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


class TravelCreateFragment : Fragment() {
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

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            saveTravelEntity()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
                    requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                )
                        && ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) -> {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }

                else -> {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
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

            findNavController().navigate(
                TravelCreateFragmentDirections.actionCreateTravelFragmentToTravelLivetrackingFragment(
                    travelEntityCreated
                )
            )
            // Navigate back or show a success message
        } else {
            // Show an error message indicating that all fields are required
        }
    }

}