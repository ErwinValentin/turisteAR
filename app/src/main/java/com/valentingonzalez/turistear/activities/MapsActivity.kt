package com.valentingonzalez.turistear.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.fragments.MapFragment
import com.valentingonzalez.turistear.fragments.MapFragment.MarkerClickedListener
import com.valentingonzalez.turistear.modal_sheets.LocationInfoModalSheet

class MapsActivity : AppCompatActivity(), MarkerClickedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, MapFragment())
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //dialogo de confirmacion
        FirebaseAuth.getInstance().signOut()
    }

    override fun markerClicked(marker: Marker?) {
        Toast.makeText(this@MapsActivity, marker!!.title, Toast.LENGTH_SHORT).show()
        val bundle = Bundle()
        bundle.putString("TITLE", marker.title)
        val info = LocationInfoModalSheet()
        info.arguments = bundle
        info.show(supportFragmentManager, "Hello")
    }
}