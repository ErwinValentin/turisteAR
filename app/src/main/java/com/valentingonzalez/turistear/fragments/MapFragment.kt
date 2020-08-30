package com.valentingonzalez.turistear.fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : SupportMapFragment(), OnMapReadyCallback, OnMarkerClickListener {
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mListener: MarkerClickedListener? = null
    var guate: Marker? = null
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mListener = if (activity is MarkerClickedListener) {
            activity
        } else {
            throw ClassCastException("$activity debe implementar el callback OnMarkerClickListener")
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    override fun onResume() {
        super.onResume()
        setUpMapIfNeeded()
    }

    private fun setUpMapIfNeeded() {
        if (!::mGoogleMap.isInitialized) {
                getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        //mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mGoogleMap.setOnPoiClickListener{poi->
            Toast.makeText(context!!,poi.name,Toast.LENGTH_SHORT ).show()
        }
        if( ContextCompat.checkSelfPermission(
                        context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mGoogleMap.isMyLocationEnabled = true

            val lastLocation = fusedLocationProviderClient.lastLocation
            lastLocation.addOnSuccessListener { location ->
                if(location != null){
                    currentLocation =location
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
                    guate = mGoogleMap.addMarker(MarkerOptions()
                            .position(LatLng(currentLocation.latitude, currentLocation.longitude))
                            .title("Marker in Guatemala")
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                }
            }
        }


        mGoogleMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker == guate) {
            mListener!!.markerClicked(marker)
        }
        return false
    }

    interface MarkerClickedListener {
        fun markerClicked(marker: Marker?)
    }
}