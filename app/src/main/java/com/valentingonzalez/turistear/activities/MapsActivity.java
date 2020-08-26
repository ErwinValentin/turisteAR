package com.valentingonzalez.turistear.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.valentingonzalez.turistear.R;
import com.valentingonzalez.turistear.fragments.MapFragment;
import com.valentingonzalez.turistear.modal_sheets.LocationInfoModalSheet;

public class MapsActivity extends AppCompatActivity implements MapFragment.MarkerClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container,new MapFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //dialogo de confirmacion
        FirebaseAuth.getInstance().signOut();
    }


    @Override
    public void markerClicked(Marker marker) {
        Toast.makeText(MapsActivity.this,marker.getTitle(), Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", marker.getTitle());
        LocationInfoModalSheet info = new LocationInfoModalSheet();
        info.setArguments(bundle);
        info.show(getSupportFragmentManager(), "Hello");

    }
}
