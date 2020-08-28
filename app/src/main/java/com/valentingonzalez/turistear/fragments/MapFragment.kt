package com.valentingonzalez.turistear.fragments

import android.app.Activity
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
    private var mGoogleMap: GoogleMap? = null
    private var mListener: MarkerClickedListener? = null
    var guate: Marker? = null
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mListener = if (activity is MarkerClickedListener) {
            activity
        } else {
            throw ClassCastException("$activity debe implementar el callback OnMarkerClickListener")
        }
    }
    
    override fun onResume() {
        super.onResume()
        setUpMapIfNeeded()
    }

    private fun setUpMapIfNeeded() {
        if (mGoogleMap == null) {
            getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val ll = LatLng(14.6229, -90.5315)
        mGoogleMap = googleMap
        guate = mGoogleMap!!.addMarker(MarkerOptions()
                .position(ll)
                .title("Marker in Guatemala")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 20f))
        mGoogleMap!!.setOnMarkerClickListener(this)
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