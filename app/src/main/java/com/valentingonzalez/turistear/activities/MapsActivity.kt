package com.valentingonzalez.turistear.activities

import android.Manifest
import android.content.Intent
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.fragments.MapFragment
import com.valentingonzalez.turistear.fragments.MapFragment.MarkerClickedListener
import com.valentingonzalez.turistear.modal_sheets.LocationInfoModalSheet
import com.valentingonzalez.turistear.models.Sitio
import kotlin.collections.HashMap

class MapsActivity : AppCompatActivity(), MarkerClickedListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionbarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private var searchDistance = 0.03
    private var searchTypes = ArrayList<String>(listOf("ALL"))
    private var searchIncludes = ""
    private var searchALL = false

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

        val accountIcon:ImageView = findViewById(R.id.toolbar_account_icon)
        accountIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        val searchIcon: ImageView = findViewById(R.id.toolbar_search_icon)
        searchIcon.setOnClickListener {
            searchALL = !searchALL
            loadMap()
            //startActivityForResult(Intent(this, SearchOptionsActivity::class.java), SEARCH_ACTIVITY_REQUEST)
        }
        accountIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(allPermisionsGranted()){
            loadMap()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

    }

//    private fun testCreateSitio() {
//        val recList = listOf<Recurso>(Recurso("image", "SDFGHJKL.jpg"),Recurso("image", "SDFGHJKL.jpg"))
//        val site = Sitio("prueba formato", 14.685027,-90.550995, "prueba movil", recList, "Museo")
//
//        val secList = listOf(Secreto("el secreto1", 14.685027,-90.550995,"NombreSecreto1",recList),Secreto("el secreto2", 14.685027,-90.550995,"NombreSecreto2",recList),Secreto("el secreto3", 14.685027,-90.550995,"NombreSecreto3",recList))
//        val siteProvider = SiteProvider(this)
//        siteProvider.createSite(site,secList)
//    }

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
        //TODO show a dialog with some predetermined locations
    }

    private fun loadMap() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val mapFragment = MapFragment()
        val argumentsBundle = Bundle()
        argumentsBundle.putDouble("DISTANCE", searchDistance)
        argumentsBundle.putStringArrayList("TYPES", searchTypes)
        argumentsBundle.putString("INCLUDES", searchIncludes)
        argumentsBundle.putBoolean("ALL", searchALL)
        mapFragment.arguments = argumentsBundle
        fragmentTransaction.add(R.id.fragment_container, mapFragment)
        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //TODO change arguments based on intent data
        //if(resultCode == SEARCH_ACTIVITY_REQUEST)
        loadMap()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //dialogo de confirmacion
        FirebaseAuth.getInstance().signOut()
    }

    override fun markerClicked(sitio: Sitio?, key: String) {
        //Toast.makeText(this@MapsActivity, marker!!.title, Toast.LENGTH_SHORT).show()
        val bundle = Bundle()
        bundle.putString(getString(R.string.marker_title), sitio!!.nombre)
        bundle.putString(getString(R.string.marker_description), sitio.descripcion)
        bundle.putString(getString(R.string.marker_location_key), key)
        bundle.putString(getString(R.string.marker_image), sitio.recursos?.get(0)?.valor)
        //bundle.putString(getString(R.string.marker_description), sitio.descripcion)
        val info = LocationInfoModalSheet()
        info.arguments = bundle
        info.show(supportFragmentManager, "Location Clicked")
    }

    companion object {
        private const val TAG = "MapsActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val SEARCH_ACTIVITY_REQUEST = 2011
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