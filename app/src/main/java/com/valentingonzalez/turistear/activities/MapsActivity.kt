package com.valentingonzalez.turistear.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.model.Marker
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.activities.camera.CameraActivity1
import com.valentingonzalez.turistear.fragments.MapFragment
import com.valentingonzalez.turistear.fragments.MapFragment.MarkerClickedListener
import com.valentingonzalez.turistear.modal_sheets.LocationInfoModalSheet
import com.valentingonzalez.turistear.models.Recurso
import com.valentingonzalez.turistear.models.Secreto
import com.valentingonzalez.turistear.models.Sitio
import com.valentingonzalez.turistear.providers.SiteProvider

class MapsActivity : AppCompatActivity(), MarkerClickedListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionbarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        drawerLayout = findViewById(R.id.maps_activity_drawer)
        actionbarToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer)

        drawerLayout.addDrawerListener(actionbarToggle)
        actionbarToggle.syncState()

        navView = findViewById(R.id.maps_navigation_view)
        navView.setNavigationItemSelectedListener(this)
        //testCreateSitio()
        val account_icon:ImageView = findViewById(R.id.toolbar_account_icon)
        account_icon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(allPermisionsGranted()){
            loadMap()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

    }

    private fun testCreateSitio() {
        val recList = listOf<Recurso>(Recurso("image", "SDFGHJKL.jpg"),Recurso("image", "SDFGHJKL.jpg"))
        val site = Sitio("prueba formato", 14.685027f,-90.550995f, "prueba movil", recList, "Museo")

        val secList = listOf(Secreto("el secreto1", 14.685027f,-90.550995f,"NombreSecreto1",recList),Secreto("el secreto2", 14.685027f,-90.550995f,"NombreSecreto2",recList),Secreto("el secreto3", 14.685027f,-90.550995f,"NombreSecreto3",recList))
        val siteProvider = SiteProvider()
        siteProvider.createSite(site,secList)
    }

    private fun allPermisionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ){
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermisionsGranted()){
                loadMap()
            }
        } else {
           showLocationSelector()
        }

    }

    private fun showLocationSelector() {
        //TODO show a diaglog with some predetermined locations
    }

    private fun loadMap() {
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

    companion object {
        private const val TAG = "MapsActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 9001
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.nav_menu_favs->{
                Toast.makeText(this, "Clicked on Favorites", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_menu_visited->{
                Toast.makeText(this, "Clicked on Visited", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_menu_settings->{
                Toast.makeText(this, "Clicked on Settings", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}