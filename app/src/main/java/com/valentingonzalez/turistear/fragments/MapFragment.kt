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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.valentingonzalez.turistear.models.Sitio
import com.valentingonzalez.turistear.providers.SiteProvider

class MapFragment : SupportMapFragment(), OnMapReadyCallback, OnMarkerClickListener, SiteProvider.DiscoveredSites {
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mListener: MarkerClickedListener? = null
    private var siteProvider: SiteProvider = SiteProvider(this)
    var guate: Marker? = null
    var marcadores: HashMap<Marker,Sitio> = HashMap()
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
            siteProvider.getAllSites(marcadores,mGoogleMap)
            val lastLocation = fusedLocationProviderClient.lastLocation
            lastLocation.addOnSuccessListener { location ->
                if(location != null){
                    currentLocation =location
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
                }
            }

        }
        mGoogleMap.setOnMarkerClickListener(this)
    }

    private fun addAllMarkers(sitios: List<Sitio>) {

        for (s in sitios){
            val marker = mGoogleMap.addMarker(MarkerOptions()
                    .position(LatLng(s.latitud!!,s.longitud!!))
                    .title(s.nombre))

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        mListener!!.markerClicked(marcadores[marker], marker.tag.toString())
        return false
    }

    interface MarkerClickedListener {
        fun markerClicked(sitio: Sitio?, key: String)
    }

    override fun onDiscovered(lista: List<Boolean>) {

    }
}
