package com.valentingonzalez.turistear.activities.maps

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.fragments.MapFragment
import com.valentingonzalez.turistear.fragments.MapFragment.MarkerClickedListener
import com.valentingonzalez.turistear.fragments.RoutesFragment
import com.valentingonzalez.turistear.fragments.UserFavoritesFragment
import com.valentingonzalez.turistear.fragments.VisitedFragment
import com.valentingonzalez.turistear.modal_sheets.LocationInfoModalSheet
import com.valentingonzalez.turistear.models.FavoritoUsuario
import com.valentingonzalez.turistear.models.Sitio
import com.valentingonzalez.turistear.models.SitioDescubierto
import com.valentingonzalez.turistear.models.Usuario
import com.valentingonzalez.turistear.providers.UserProvider
import dmax.dialog.SpotsDialog
import java.util.ArrayList

class MapsActivity : AppCompatActivity(), MarkerClickedListener, NavigationView.OnNavigationItemSelectedListener, UserProvider.UserProviderListener, UserFavoritesFragment.FavoriteFragmentInterface, VisitedFragment.VisitedFragmentInterface{

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionbarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var accountIcon:ImageView
    private lateinit var searchIcon: ImageView
    private lateinit var levelProgressBar: ProgressBar
    private lateinit var currentLevel: TextView
    private lateinit var levelProgressText: TextView
    private lateinit var userName: TextView
    private lateinit var userPoints: TextView

    private var userProvider = UserProvider(this)
    private var searchDistance = 10
    private var searchTypes = ArrayList<String>(listOf())
    private var searchIncludes = ""
    private var searchALL = false

    var progessDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        userProvider.getUser()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        drawerLayout = findViewById(R.id.maps_activity_drawer)
        actionbarToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(actionbarToggle)
        actionbarToggle.syncState()
        navView = findViewById(R.id.maps_navigation_view)
        navView.setNavigationItemSelectedListener(this)
        accountIcon = findViewById(R.id.toolbar_account_icon)
        accountIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        searchIcon = findViewById(R.id.toolbar_search_icon)
        searchIcon.setOnClickListener {
            startActivityForResult(Intent(this, SearchOptionsActivity::class.java), SEARCH_ACTIVITY_REQUEST)
        }
        levelProgressBar = findViewById(R.id.level_progress_bar)
        currentLevel = findViewById(R.id.level_view)
        levelProgressText = findViewById(R.id.level_progress_text)
        userName = navView.getHeaderView(0).findViewById(R.id.nav_drawer_user_name)
        userPoints = navView.getHeaderView(0).findViewById(R.id.nav_drawer_user_current_points)
        progessDialog = SpotsDialog.Builder().setContext(this).setMessage("Conectando...").build()
        progessDialog!!.show()
        userProvider.getUser()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(allPermisionsGranted()){
            loadMap("", -2)
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
                loadMap("", -2)
            }
        } else {
           showLocationSelector()
        }

    }

    private fun showLocationSelector() {
        //TODO show a dialog with some predetermined locations
    }

    private fun loadMap(favorito : String, numSecreto: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val mapFragment = MapFragment()
        val argumentsBundle = Bundle()
        argumentsBundle.putInt("DISTANCE", searchDistance)
        argumentsBundle.putStringArrayList("TYPES", searchTypes)
        argumentsBundle.putString("INCLUDES", searchIncludes)
        argumentsBundle.putBoolean("ALL", searchALL)
        argumentsBundle.putString("FAVORITO", favorito)
        argumentsBundle.putInt("SECRETO", numSecreto)
        mapFragment.arguments = argumentsBundle
        fragmentTransaction.add(R.id.fragment_container, mapFragment)
        fragmentTransaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null && resultCode == Activity.RESULT_OK) {
            //if(resultCode == SEARCH_ACTIVITY_REQUEST)
            searchTypes.clear()
            searchTypes = data.getStringArrayListExtra("TYPES")!!
            searchIncludes = data.getStringExtra("CONTAINS")!!
            searchDistance = data.getIntExtra("DISTANCE", 0)
            Log.d("DISTANCE", data.getIntExtra("DISTANCE", 0).toString())
            Log.d("TYPES", data.getStringArrayListExtra("TYPES")!!.toString())
            Log.d("CONTAINS", data.getStringExtra("CONTAINS")!!.toString())
            loadMap("", -2)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //TODO dialogo de confirmacion
        FirebaseAuth.getInstance().signOut()
    }

    override fun markerClicked(sitio: Sitio?, key: String) {
        //Toast.makeText(this@MapsActivity, marker!!.title, Toast.LENGTH_SHORT).show()
        val bundle = Bundle()
        Log.d("SITIOMODAL", sitio.toString())
        bundle.putString(getString(R.string.marker_title), sitio!!.nombre)
        bundle.putString(getString(R.string.marker_description), sitio.descripcion)
        bundle.putString(getString(R.string.marker_location_key), key)
        bundle.putString(getString(R.string.marker_image), sitio.recursos?.get(0)?.valor)
        bundle.putDouble(getString(R.string.marker_location_rating), sitio.rating!!)
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
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val argumentsBundle = Bundle()
        argumentsBundle.putString("USERID", FirebaseAuth.getInstance().uid.toString())
        when (item.itemId){
            R.id.nav_menu_map ->{
                loadMap("",-2)
            }
            R.id.nav_menu_route ->{
                val routeFragment = RoutesFragment()
                fragmentTransaction.add(R.id.fragment_container, routeFragment)
            }
            R.id.nav_menu_favs->{
                val favFragment = UserFavoritesFragment()
                favFragment.arguments = argumentsBundle
                fragmentTransaction.add(R.id.fragment_container, favFragment)

            }
            R.id.nav_menu_visited->{
                val visFragment = VisitedFragment(FirebaseAuth.getInstance().uid.toString())
                fragmentTransaction.add(R.id.fragment_container, visFragment)
            }
            R.id.nav_menu_shop ->{
                Toast.makeText(this, "Tienda proximamente", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_menu_settings->{
                Toast.makeText(this, "Clicked on Settings", Toast.LENGTH_SHORT).show()
            }
        }
        fragmentTransaction.commit()
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFavoriteChecked(isFav: List<Boolean>) {
    }

    override fun getUserName(user: Usuario) {
        currentLevel.text = user.nivelActual.toString()
        val toNextLevel = user.puntosTotales!!.rem(100)
        levelProgressBar.progress = toNextLevel
        levelProgressText.text = getString(R.string.proximo_nivel_en, 100 - toNextLevel)
        userName.text = user.nombre
        userPoints.text = getString(R.string.puntos_usuario, user.puntosActuales)
        progessDialog!!.dismiss()
    }

    override fun getAllFavorites(favoritos: List<FavoritoUsuario>) {
    }

    override fun onFavoriteSelected(locationKey: String, secretNumber: Int) {

    }

    override fun gotoFavorite(favorite: FavoritoUsuario) {
        loadMap(favorite.llave!!, favorite.numSecreto!!)
    }
    //TODO  si el marcador esta fuera del rango no se muestra en el mapa, se podrían mostrar los sitios descubiertos sin importar la distancia
    override fun gotoVisited(sitio: SitioDescubierto) {
        loadMap(sitio.llave!!, -1)
    }
}