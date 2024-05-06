package com.example.travelpal.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.travelpal.R
import com.example.travelpal.databinding.DialogInputBinding

class TravelInputDialogFragment : DialogFragment() {
    private lateinit var binding: DialogInputBinding

    interface InputListener {
        fun onInputComplete(destinationName: String, description: String)
    }

    companion object {
        fun newInstance(listener: InputListener): TravelInputDialogFragment {
            val fragment = TravelInputDialogFragment()
            fragment.inputListener = listener
            return fragment
        }
    }

    private var inputListener: InputListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_input, null)
        binding = DialogInputBinding.bind(view)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val description = binding.etDescription
            val destinationName = binding.etDestinationName

            builder.setView(view)
                .setPositiveButton(R.string.save) { _, _ ->
                    val input1 = description.text.toString()
                    val input2 = destinationName.text.toString()
                    inputListener?.onInputComplete(input1, input2)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setInputListener(listener: InputListener) {
        inputListener = listener
    }
}