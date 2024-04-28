package com.example.travelpal.ui.manager

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
class PermissionsManager(private val fragment: Fragment) {
    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    }
    fun checkAndRequestPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA)
        val loc = ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        val loc2 = ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                fragment.requireActivity(),
                listPermissionsNeeded.toTypedArray<String>(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
        }
        return true
    }

    fun checkPermissions(): Boolean {
        val camera = ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA)
        val loc = ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        val loc2 = ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        return camera == PackageManager.PERMISSION_GRANTED && loc == PackageManager.PERMISSION_GRANTED && loc2 == PackageManager.PERMISSION_GRANTED
    }

}