package com.valentingonzalez.turistear.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.valentingonzalez.turistear.activities.MapsActivity;

public class MapFragment extends SupportMapFragment
        implements OnMapReadyCallback, OnMarkerClickListener {

    private GoogleMap mGoogleMap;
    MarkerClickedListener mListener;
    Marker guate;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MarkerClickedListener){
            mListener = (MarkerClickedListener) activity;
        }else{
            throw new ClassCastException(activity.toString()+" debe implementar el callback OnMarkerClickListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mGoogleMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        LatLng ll = new LatLng( 14.6229,-90.5315);
        mGoogleMap=googleMap;


        guate = mGoogleMap.addMarker(new MarkerOptions()
                .position(ll)
                .title("Marker in Guatemala")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 5));
        mGoogleMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(guate)){
            mListener.markerClicked(marker);
        }
        return false;
    }

    public interface MarkerClickedListener{
        void markerClicked(Marker marker);
    }
}