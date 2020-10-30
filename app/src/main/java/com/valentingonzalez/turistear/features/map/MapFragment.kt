package com.valentingonzalez.turistear.features.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.PolyUtil
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.features.secret.SecretDetailActivity
import com.valentingonzalez.turistear.features.map.maputils.CustomInfoWindow
import com.valentingonzalez.turistear.models.*
import com.valentingonzalez.turistear.providers.SiteProvider
import com.valentingonzalez.turistear.features.map.maputils.RouteDistanceComparator
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.indices as indices


class MapFragment : SupportMapFragment(), OnMapReadyCallback, OnMarkerClickListener, SiteProvider.SiteInterface{
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mListener: MarkerClickedListener? = null
    private var siteProvider: SiteProvider = SiteProvider(this)

    private lateinit var selectedFavorite: String
    private var selectedSecret: Int = -1
    private var selectedRoute : Ruta? = null

    var marcadores: HashMap<Marker,Sitio> = HashMap()
    val userId = FirebaseAuth.getInstance().uid!!
    var markerKey : String =""
    var markerName : String = ""

    override fun onAttach(context: Context) {
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
//        mGoogleMap.setOnPoiClickListener{poi->
//            Toast.makeText(context!!,poi.name,Toast.LENGTH_SHORT ).show()
//        }
        mGoogleMap.setInfoWindowAdapter(CustomInfoWindow(LayoutInflater.from(context)))
        if( ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mGoogleMap.isMyLocationEnabled = true
            var searchAll = false
            if(arguments != null){
                searchAll  = arguments!!.getBoolean("ALL", false)
            }

            val searchDistance = arguments?.getInt("DISTANCE")!!
            val searchTypes  = arguments?.getStringArrayList("TYPES")!!
            val searchIncludes = arguments?.getString("INCLUDES")!!
            selectedFavorite = arguments?.getString("FAVORITO")!!
            selectedSecret = arguments?.getInt("SECRETO")!!

            val lastLocation = fusedLocationProviderClient.lastLocation
            lastLocation.addOnSuccessListener { location ->
                if(location != null){
                    currentLocation =location
                    try{
                        selectedRoute = arguments?.getSerializable("RUTA") as Ruta

                        Log.d("RUTA SELECCIONADA", selectedRoute.toString())
                        drawPolyline(selectedRoute!!, location)
                    }catch (npe : NullPointerException){
                        npe.printStackTrace()
                    }
                    if(selectedFavorite.isEmpty()){
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
                    }
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

    private fun drawPolyline(selectedRoute: Ruta, location: Location) {
        val listaPuntos = selectedRoute.puntos!!
        val currentLatLng = LatLng(location.latitude, location.longitude)
        Collections.sort(listaPuntos, RouteDistanceComparator(currentLatLng))

        val paths: MutableList<List<LatLng>> = ArrayList()
        val urlRequest = makeURL(listaPuntos, location)

        val directionsRequest = object: StringRequest(Request.Method.GET, urlRequest,
                                    Response.Listener<String>{
                                        response ->
                                        Log.d("RESPONSE", response.toString())
                                        val jsonResponse = JSONObject(response)
                                        val routes = jsonResponse.getJSONArray("routes")
                                        val legs = routes.getJSONObject(0).getJSONArray("legs")
                                        val steps : MutableList<JSONArray> = ArrayList()
                                        for (i in 0 until legs.length()){
                                            val step = legs.getJSONObject(i).getJSONArray("steps")
                                            steps.add(step)
                                        }
                                        for(i in 0 until steps.size){
                                            for(j in 0 until steps[i].length()){
                                                val point = steps[i].getJSONObject(j).getJSONObject("polyline").getString("points")
                                                paths.add(PolyUtil.decode(point))
                                            }
                                        }
                                        for (i in 0 until paths.size){
                                            mGoogleMap.addPolyline(PolylineOptions().addAll(paths[i]).color(Color.RED))
                                        }

                                    }, Response.ErrorListener {  }
                                    ){}
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(directionsRequest)
    }
    private fun makeURL(puntos: List<PuntoRuta>, location: Location): String{
        val baseUrl = "https://maps.googleapis.com/maps/api/directions/json?"
        val origin = "origin=${location.latitude},${location.longitude}"
        val destination = "&destination=${puntos[puntos.size-1].latitud},${puntos[puntos.size-1].longitud}"
        var waypoints = "&waypoints="
        for( i in puntos.indices-1){
            Log.d("PUNTOS", i.toString())
            waypoints+= "${puntos[i].latitud},${puntos[i].longitud}"
            if(i<puntos.size-1){
                waypoints+="|"
            }
        }
        val apiKey = "&key=${getString(R.string.google_maps_key)}"
        val returnPath = baseUrl+origin+destination+waypoints+apiKey
        Log.d("REQUEST", returnPath)
        return returnPath
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
        markerKey = marker.tag.toString()
        markerName = marker.title
        if((currentLocation.distanceTo(loc))<100){
            siteProvider.getDiscoveredSites(userId)
        }
        return false
    }

    interface MarkerClickedListener {
        fun markerClicked(sitio: Sitio?, key: String)
    }

    override fun userDiscovered(list: List<SitioDescubierto>) {
        for(sitio in list){
            if(sitio.llave == markerKey){
                return
            }
        }
        siteProvider.addSiteToDiscovered(markerKey, userId, markerName, Calendar.getInstance().time.toString(), true)
        Toast.makeText(context,  "¡Has descubierto un sitio nuevo!", Toast.LENGTH_SHORT).show()
    }

    override fun listReady() {
        if(selectedFavorite.isNotEmpty()) {
            siteProvider.getSite(selectedFavorite)
        }
    }

    override fun typesFound(list: ArrayList<String>) {
    }

    override fun getSingleSite(site: Sitio, key: String) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(site.latitud!!, site.longitud!!), 15f))
        val marker = mGoogleMap.addMarker(MarkerOptions()
                .position(LatLng(site.latitud!!,site.longitud!!))
                .title(site.nombre)
                .snippet(site.tipo))
        marker.tag = key
        try{
            marcadores[marker] = site
        }catch (e: Exception){
            e.printStackTrace()
        }
//        if(favoriteSelected.isNotEmpty()){
//            for(marcador in marcadores.keys){
//                if(marcador.tag == favoriteSelected){
//                    marcador.showInfoWindow()
//                    break
//                }
//            }
//        }
        marker.showInfoWindow()
        if(selectedSecret >= 0){
            val intent = Intent(context, SecretDetailActivity::class.java)
            intent.putExtra(getString(R.string.marker_location_key), key)
            startActivity(intent)
        }
    }
}
