package com.valentingonzalez.turistear.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
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
import java.util.ArrayList

class MapFragment : SupportMapFragment(), OnMapReadyCallback, OnMarkerClickListener{
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mListener: MarkerClickedListener? = null
    private var siteProvider: SiteProvider = SiteProvider()
    var marcadores: HashMap<Marker,Sitio> = HashMap()

    override fun onAttach(context: Context) {
        //TODO receive marker options and display the correct markers
        super.onAttach(context)
        try {
            mListener = context as MarkerClickedListener
        } catch (e: java.lang.ClassCastException){
            throw ClassCastException("$activity debe implementar el callback OnMarkerClickListener")
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

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

        if( ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mGoogleMap.isMyLocationEnabled = true
            val searchAll: Boolean = arguments!!.getBoolean("ALL")
            val searchDistance = arguments!!.getDouble("DISTANCE")
            val searchTypes: ArrayList<String>? = arguments!!.getStringArrayList("TYPES")
            val searchIncludes: String? = arguments!!.getString("INCLUDES")

            val lastLocation = fusedLocationProviderClient.lastLocation
            lastLocation.addOnSuccessListener { location ->
                if(location != null){
                    currentLocation =location
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
                    /*TODO pasar seach distance,
                    *  if(searchTypes[0] != "ALL") filtrar marcadores por tipo
                    *  if(searchIncludes != "") filtrar por nombre*/
                    showNearby(location, marcadores, mGoogleMap, searchAll)
                }
            }

        }

        mGoogleMap.setOnMarkerClickListener(this)
    }

    private fun showNearby(location: Location, marcadores: HashMap<Marker,Sitio>, map: GoogleMap,b: Boolean) {
        if(b){
            siteProvider.getNearbySites(location.latitude, location.longitude, marcadores, mGoogleMap)
        }else{
            siteProvider.getAllSites(marcadores,mGoogleMap)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        mListener!!.markerClicked(marcadores[marker], marker.tag.toString())
        return false
    }

    interface MarkerClickedListener {
        fun markerClicked(sitio: Sitio?, key: String)
    }
}
