package com.valentingonzalez.turistear.providers

import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.Secreto
import com.valentingonzalez.turistear.models.Sitio

class SiteProvider{
    private var mSiteReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Sitios")
    private var mSecretReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Secretos")

    fun createSite(sitio: Sitio, secrets: List<Secreto>): Task<Void> {
        val key = mSiteReference.push().key!!
        mSecretReference.child(key).setValue(secrets)
        return mSiteReference.child(key).setValue(sitio)
    }
    fun getSites(latitud: Double, longitud: Double, distancia: Int?, tipos: ArrayList<String>?, titleContains: String?,markers: HashMap<Marker, Sitio>, map: GoogleMap, all: Boolean ){
        if(all){
            getAllSites(markers, map)
        }else{
            if(distancia!= null && tipos==null && titleContains == null){
                getNearbySites(latitud, longitud, distancia ,markers, map)
            }
            if(distancia== null && tipos!=null && titleContains == null) {
                getSitesByType(latitud, longitud, tipos, markers, map)
            }
            if(distancia== null && tipos==null && titleContains != null) {
                getSitesByTitle(latitud, longitud, titleContains , markers, map)
            }
            if(distancia!= null && tipos!=null && titleContains == null) {
                getSitesByDistanceAndType(latitud, longitud, distancia, tipos, markers, map)
            }
            if(distancia!= null && tipos==null && titleContains != null) {
                getSitesByDistanceAndTitle(latitud, longitud, distancia, titleContains, markers, map)
            }
            if(distancia== null && tipos!=null && titleContains != null) {
                getSitesByTypeAndTitle(latitud, longitud, tipos, titleContains, markers, map)
            }
            if(distancia!= null && tipos!=null && titleContains != null) {
                getSitesWithAllConditions(latitud, longitud, distancia, tipos, titleContains, markers, map)
            }
        }
    }

    fun getAllSites(lista: HashMap<Marker,Sitio>, map: GoogleMap){
        mSiteReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(site in snapshot.children){
                        Log.d("SITIOS", site.toString())
                        val s = site.getValue(Sitio::class.java)

                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(s?.latitud!!,s.longitud!!))
                                .title(s.nombre))
                        marker.tag = site.key
                        lista[marker] = s
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun getNearbySites(latitude: Double, longitud: Double,distancia: Int, marcadores: HashMap<Marker, Sitio>, map: GoogleMap){
        //TODO convertir distancia a las coordenadas y cambiar el 0.03 por la distancia especificada
        mSiteReference.orderByChild("latitud").startAt(latitude-0.03).endAt(latitude+0.003).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for(marcador in marcadores.keys){
                    marcador.remove()
                }

                for(data in snapshot.children){

                    val d:Sitio = data.getValue(Sitio::class.java)!!

                    if(d.longitud!! <= longitud+0.003 && d.longitud!!>=longitud-0.003){
                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(d.latitud!!,d.longitud!!))
                                .title(d.nombre))
                        marker.tag = data.key
                        marcadores[marker] = d
                    }

                }

            }

        })
    }

    fun getSitesByType(latitude: Double, longitud: Double, tipos: ArrayList<String>, marcadores: HashMap<Marker, Sitio>, map: GoogleMap){
    }
    fun getSitesByTitle(latitude: Double, longitud: Double, titulo: String, marcadores: HashMap<Marker, Sitio>, map: GoogleMap){
    }
    fun getSitesByDistanceAndType(latitude: Double, longitud: Double, distancia: Int, tipos: ArrayList<String>, marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
    }
    fun getSitesByDistanceAndTitle(latitude: Double, longitud: Double, distancia: Int, titulo: String , marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
    }
    fun getSitesByTypeAndTitle(latitude: Double, longitud: Double, tipos: ArrayList<String>, titulo: String, marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
    }
    fun getSitesWithAllConditions(latitude: Double, longitud: Double, distancia: Int, tipos: ArrayList<String>, titulo: String, marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
    }
//    interface DiscoveredSites{
//        fun onDiscovered(lista: List<Boolean>)
//    }
}