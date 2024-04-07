package com.example.travelpal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelpal.databinding.FragmentTravelCreateBinding
import com.example.travelpal.repository.TravelRepository

class CreateTravelFragment : Fragment(){
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSave.setOnClickListener {
            saveTravelEntity()
        }
    }

    private fun saveTravelEntity() {
        val destinationName = binding.etDestinationName.text.toString()
        val date = binding.etDate.text.toString()
        val description = binding.etDescription.text.toString()

        if (destinationName.isNotEmpty() && date.isNotEmpty() && description.isNotEmpty()) {
            travelRepository.createTravel(destinationName, date, description)
            findNavController().navigate(CreateTravelFragmentDirections.actionCreateTravelFragmentToTravelListFragment())
            // Navigate back or show a success message
        } else {
            // Show an error message indicating that all fields are required
        }
    }
}