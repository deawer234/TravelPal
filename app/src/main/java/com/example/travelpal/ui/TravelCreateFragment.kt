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
import java.util.Calendar


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
            && permissions[Manifest.permission.ACTIVITY_RECOGNITION] == true
        ) {
            saveTravelEntity()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        binding.etDate.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )

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
                        && ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
                -> {
                    saveTravelEntity()
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
                -> {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }

                else -> {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACTIVITY_RECOGNITION
                        )
                    )
                }
            }
        }
    }


    private fun saveTravelEntity() {
        val destinationName = binding.etDestinationName.text.toString()
        val date = binding.etDate.dayOfMonth.toString() + "/" +
                binding.etDate.month.toString() + "/" +
                binding.etDate.year.toString()

        val description = binding.etDescription.text.toString()

        if (destinationName.isNotEmpty() && date.isNotEmpty() && description.isNotEmpty()) {
            //Temporary measure
            val travelEntity = TravelEntity(
                destinationName = destinationName,
                date = date,
                description = description,
                mapThumbnail = null
            )
            val id = travelRepository.createTravel(travelEntity)
            val travelEntityCreated = travelRepository.getTravelById(id)

            Intent(requireActivity().applicationContext, TrackerService::class.java).apply {
                action = TrackerService.ACTION_START
                putExtra("travelEntityId", id)
                requireActivity().startService(this)
            }

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