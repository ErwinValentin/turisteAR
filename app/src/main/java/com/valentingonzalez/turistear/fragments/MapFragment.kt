package com.valentingonzalez.turistear.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.models.Sitio
import com.valentingonzalez.turistear.providers.SiteProvider
import java.util.*
import kotlin.collections.HashMap


class MapFragment : SupportMapFragment(), OnMapReadyCallback, OnMarkerClickListener, SiteProvider.SiteInterface{
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mListener: MarkerClickedListener? = null
    private var siteProvider: SiteProvider = SiteProvider(this)
    var marcadores: HashMap<Marker,Sitio> = HashMap()
    val userId = FirebaseAuth.getInstance().uid!!


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
            var searchAll = false
            if(arguments != null){
                searchAll  = arguments!!.getBoolean("ALL", false)
            }

            val searchDistance = arguments?.getInt("DISTANCE")!!
            val searchTypes  = arguments?.getStringArrayList("TYPES")!!
            val searchIncludes = arguments?.getString("INCLUDES")!!

            val lastLocation = fusedLocationProviderClient.lastLocation
            lastLocation.addOnSuccessListener { location ->
                if(location != null){
                    currentLocation =location
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
                    /*TODO pasar seach distance,
                    *  if(searchTypes[0] != "ALL") filtrar marcadores por tipo
                    *  if(searchIncludes != "") filtrar por nombre*/
//                    Log.d("SEARCHPARAMSALL",searchAll.toString())
//                    Log.d("SEARCHPARAMSDISTANCE",searchDistance.toString())
//                    Log.d("SEARCHPARAMSTYPES",searchTypes.toString())
//                    Log.d("SEARCHPARAMSINCLUDE",searchIncludes+" jpasd")
                    siteProvider.getSites(location.latitude, location.longitude, searchDistance ,searchTypes, searchIncludes, marcadores, mGoogleMap, searchAll)
                }
            }

        }

        mGoogleMap.setOnMarkerClickListener(this)
    }

//    private fun showNearby(location: Location, marcadores: HashMap<Marker,Sitio>, map: GoogleMap,b: Boolean) {
//        if(b){
//            siteProvider.getNearbySites(location.latitude, location.longitude, marcadores, mGoogleMap)
//        }else{
//            siteProvider.getAllSites(marcadores,mGoogleMap)
//        }
//    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val loc: Location = Location("")
        loc.longitude = marker.position.longitude
        loc.latitude = marker.position.latitude
        Log.d("DISTANCIA",currentLocation.distanceTo(loc).toString())
        mListener!!.markerClicked(marcadores[marker], marker.tag.toString())
        if((currentLocation.distanceTo(loc))<100){
            siteProvider.addSiteToDiscovered(marker.tag.toString(), userId, marker.title, Calendar.getInstance().time.toString(), true)
            Toast.makeText(context,  "¡Has descubierto un sitio nuevo!", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    interface MarkerClickedListener {
        fun markerClicked(sitio: Sitio?, key: String)
    }

    override fun sitesFound(size: Int) {

        if(size == 0){
            Toast.makeText(context, "No sites found, please change parameters", Toast.LENGTH_SHORT).show()
        }
    }

    override fun typesFound(list: ArrayList<String>) {
    }
}
