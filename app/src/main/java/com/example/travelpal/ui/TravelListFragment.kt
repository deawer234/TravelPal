package com.example.travelpal.ui

import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpal.databinding.FragmentTravelListBinding
import com.example.travelpal.repository.TravelRepository
import com.example.travelpal.ui.adapter.TravelAdapter
import com.example.travelpal.ui.manager.LiveTrackingManager
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TravelListFragment : Fragment() {
    companion object {
        private const val REQUEST_CODE = 1
    }

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
            showTrackingOptionsDialog()
        }
        binding.rvTravelEntries.layoutManager = LinearLayoutManager(requireContext());
        binding.rvTravelEntries.adapter = adapter

        itemTouchHelper.attachToRecyclerView(binding.rvTravelEntries)
    }

    override fun onResume() {
        super.onResume()

        adapter.submitList(travelRepository.getAllTravels())
    }

    // Sliding delete magic

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val item = adapter.currentList[position]

            val newList = adapter.currentList.toMutableList().apply { removeAt(position) }
            adapter.submitList(newList)

            // Show the Snackbar with the UNDO option
            Snackbar.make(binding.rvTravelEntries, "Item deleted", Snackbar.LENGTH_LONG).apply {
                setAction("UNDO") {
                    newList.add(position, item)
                    adapter.submitList(newList.toList())
                    adapter.notifyItemInserted(position)
                }
                addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event != DISMISS_EVENT_ACTION) {
                            travelRepository.deleteTravel(item)
                        }
                    }
                })
                show()
            }
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val itemView = viewHolder.itemView
                val paint = Paint()

                if (dX < 0) {
                    paint.color = Color.RED
                    c.drawRect(itemView.left.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), paint)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
    }
    val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

    private fun showTrackingOptionsDialog() {
        val options = arrayOf("Insert from previous travel", "Start live tracking")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> findNavController().navigate(TravelListFragmentDirections.actionTravelListFragmentToCreateTravelFragment())
                1 -> {
                    println("Test")
                    getTrackingPermissions()
                    if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        findNavController().navigate(TravelListFragmentDirections.actionTravelListFragmentToTravelLivetrackingFragment())
                    }
                }

            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun getTrackingPermissions(){
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                Companion.REQUEST_CODE
            )
            return
        }
    }



}