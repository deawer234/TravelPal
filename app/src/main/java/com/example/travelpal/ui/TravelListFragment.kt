package com.example.travelpal.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpal.databinding.FragmentTravelListBinding
import com.example.travelpal.repository.TravelRepository
import com.example.travelpal.ui.adapter.TravelAdapter
import com.google.android.material.snackbar.Snackbar

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
            findNavController().navigate(TravelListFragmentDirections.actionTravelListFragmentToTravelDetailFragment(travel))
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
            findNavController().navigate(TravelListFragmentDirections.actionTravelListFragmentToCreateTravelFragment())
        }
        binding.rvTravelEntries.layoutManager = LinearLayoutManager(requireContext());
        binding.rvTravelEntries.adapter = adapter

        itemTouchHelper.attachToRecyclerView(binding.rvTravelEntries)
    }

    override fun onResume() {
        super.onResume()

        adapter.submitList(travelRepository.getAllTravels())
    }

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            // Drag and drop functionality is not needed here
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val item = adapter.currentList[position]

            // Delete the item from the database
            travelRepository.deleteTravel(item)

            // Optionally, show an undo Snackbar
            Snackbar.make(binding.rvTravelEntries, "Item deleted", Snackbar.LENGTH_LONG).setAction("UNDO") {
                travelRepository.createTravel(item.destinationName, item.date, item.description) // Re-insert the item on undo
                val updatedList = travelRepository.getAllTravels()
                adapter.submitList(updatedList)
            }.show()
        }
    }

    // Attach it to the RecyclerView
    val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

}