package com.example.travelpal.ui

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelpal.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.Polygon

class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.map_activity)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        setUiSetting(googleMap)

        val brno = LatLng(49.1951, 16.6068)
        val vienna = LatLng(48.2082, 16.3738)

        googleMap.addMarker(MarkerOptions().position(brno).title("marker in Brno"))
        googleMap.addMarker(MarkerOptions().position(vienna).title("marker in Vienna"))

        googleMap.addPolyline(PolylineOptions()
            .clickable(true)
            .add(
                brno,
                vienna))


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brno, 8f))

        googleMap.setOnPolylineClickListener(this)
        googleMap.setOnPolygonClickListener(this)
    }


    override fun onPolylineClick(polyline: Polyline) {
        polyline.color = Color.BLUE
    }

    override fun onPolygonClick(polygon: Polygon) {

        polygon.fillColor = Color.BLUE
    }

    fun setUiSetting(googleMap: GoogleMap){
        val uiSettings = googleMap.uiSettings

        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = true
    }
}
