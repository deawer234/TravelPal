package com.example.travelpal.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.travelpal.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MapActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val uiSettings = map.uiSettings

        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isRotateGesturesEnabled = true

        map.setOnPolylineClickListener(this)
        map.setOnPolygonClickListener(this)

        val brno = LatLng(49.1951, 16.6068)
        val vienna = LatLng(48.2082, 16.3738)

        map.addMarker(MarkerOptions().position(brno).title("Marker in Brno"))
        map.addMarker(MarkerOptions().position(vienna).title("Marker in Vienna"))

        fetchDirections(brno, vienna)

        val bounds = LatLngBounds.builder().include(brno).include(vienna).build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun fetchDirections(origin: LatLng, destination: LatLng) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = URL("https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&key=YOUR_API_KEY").readText()
            val polyline = extractPolylineFromResponse(response)
            withContext(Dispatchers.Main) {
                drawRouteOnMap(map, polyline)
            }
        }
    }

    private fun drawRouteOnMap(map: GoogleMap, polylineData: String) {
        val decodedPath = PolyUtil.decode(polylineData)
        val polyline = map.addPolyline(PolylineOptions().addAll(decodedPath).width(10f).color(Color.RED).clickable(true))
        polyline.tag = "A"
    }

    private fun extractPolylineFromResponse(response: String): String {
        val jsonObject = JSONObject(response)
        val routes = jsonObject.getJSONArray("routes")
        if (routes.length() > 0) {
            val overviewPolylines = routes.getJSONObject(0).getJSONObject("overview_polyline")
            return overviewPolylines.getString("points")
        }
        return ""
    }


    override fun onPolylineClick(polyline: Polyline) {
        polyline.color = Color.BLUE
    }

    override fun onPolygonClick(polygon: Polygon) {

        polygon.fillColor = Color.BLUE
    }
}
