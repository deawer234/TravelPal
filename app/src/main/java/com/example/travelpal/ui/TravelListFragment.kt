package com.example.travelpal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelpal.R
import com.example.travelpal.databinding.FragmentTravelListBinding
import com.example.travelpal.repository.TravelRepository
import com.example.travelpal.ui.adapter.TravelAdapter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TravelListFragment : Fragment() {

    private lateinit var binding: FragmentTravelListBinding


    private val travelRepository: TravelRepository by lazy {
        TravelRepository(requireContext())
    }

    private val adapter = TravelAdapter(
        onClick = { travel ->
            Toast.makeText(context, "${travel.destinationName} ", Toast.LENGTH_LONG)
                .show()
        }
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTravelListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddTravelEntry.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.rvTravelEntries.layoutManager = LinearLayoutManager(requireContext());
        binding.rvTravelEntries.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        adapter.submitList(travelRepository.getAllTravels())
    }
}