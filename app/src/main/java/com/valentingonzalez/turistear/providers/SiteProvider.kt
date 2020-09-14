package com.valentingonzalez.turistear.providers

import android.util.Log
import androidx.annotation.Nullable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.Secreto
import com.valentingonzalez.turistear.models.Sitio
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("UNUSED_PARAMETER")
class SiteProvider(@Nullable var listener: SiteInterface?){
    private var mSiteReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Sitios")
    private var mSecretReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Secretos")

    fun createSite(sitio: Sitio, secrets: List<Secreto>): Task<Void> {
        val key = mSiteReference.push().key!!
        mSecretReference.child(key).setValue(secrets)
        return mSiteReference.child(key).setValue(sitio)
    }
    fun getSites(latitud: Double, longitud: Double, distancia: Int, tipos: ArrayList<String>, titleContains: String,markers: HashMap<Marker, Sitio>, map: GoogleMap, all: Boolean ){
        if(all || (distancia== 0 && tipos.size == 0 && titleContains.isEmpty())){
            getAllSites(markers, map)
            Log.d("SITEPROVIDER", "CALLED getAll")
        }else{
            if(distancia!= 0 && tipos.size == 0 && titleContains.isEmpty()){
                getNearbySites(latitud, longitud, distancia ,markers, map)
                Log.d("SITEPROVIDER", "CALLED getAll")
                return
            }
            if(distancia== 0 && tipos.size != 0 && titleContains.isEmpty()) {
                Log.d("SITEPROVIDER", "CALLED getSitesByType")
                getSitesByType(latitud, longitud, tipos, markers, map)
                return
            }
            if(distancia== 0 && tipos.size == 0 && titleContains.isNotEmpty()) {
                Log.d("SITEPROVIDER", "CALLED getSitesByTitle")
                getSitesByTitle(latitud, longitud, titleContains , markers, map)
                return
            }
            if(distancia!= 0 && tipos.size != 0 && titleContains.isEmpty()) {
                Log.d("SITEPROVIDER", "CALLED getSitesByDistanceAndType")
                getSitesByDistanceAndType(latitud, longitud, distancia, tipos, markers, map)
                return
            }
            if(distancia!= 0 && tipos.size == 0 && titleContains.isNotEmpty()) {
                Log.d("SITEPROVIDER", "CALLED getSitesByDistanceAndTitle")
                getSitesByDistanceAndTitle(latitud, longitud, distancia, titleContains, markers, map)
                return
            }
            if(distancia== 0 && tipos.size != 0 && titleContains.isNotEmpty()) {
                Log.d("SITEPROVIDER", "CALLED getSitesByTypeAndTitle")
                getSitesByTypeAndTitle(latitud, longitud, tipos, titleContains, markers, map)
                return
            }
            if(distancia!= 0 && tipos.size != 0 && titleContains.isNotEmpty()) {
                Log.d("SITEPROVIDER", "CALLED getSitesWithAllConditions")
                getSitesWithAllConditions(latitud, longitud, distancia, tipos, titleContains, markers, map)
                return
            }
            Log.d("SITEPROVIDER", "CALLED None")
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
        mSiteReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(marcador in marcadores.keys){
                    marcador.remove()
                }
                for( data in snapshot.children){
                    val d = data.getValue(Sitio::class.java)!!
                    if(tipos.contains(d.tipo)){
                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(d.latitud!!,d.longitud!!))
                                .title(d.nombre))
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.sitesFound(marcadores.size)
            }
        })
    }
    fun getSitesByTitle(latitude: Double, longitud: Double, titulo: String, marcadores: HashMap<Marker, Sitio>, map: GoogleMap){
        mSiteReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(marcador in marcadores.keys){
                    marcador.remove()
                }
                for( data in snapshot.children){
                    val d = data.getValue(Sitio::class.java)!!
                    if(d.nombre!!.toLowerCase(Locale.getDefault()).contains(titulo.toLowerCase(Locale.getDefault()))){
                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(d.latitud!!,d.longitud!!))
                                .title(d.nombre))
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.sitesFound(marcadores.size)
            }
        })
    }
    fun getSitesByDistanceAndType(latitude: Double, longitud: Double, distancia: Int, tipos: ArrayList<String>, marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
        TODO()
    }
    fun getSitesByDistanceAndTitle(latitude: Double, longitud: Double, distancia: Int, titulo: String , marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
        TODO()
    }
    fun getSitesByTypeAndTitle(latitude: Double, longitud: Double, tipos: ArrayList<String>, titulo: String, marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
        mSiteReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(marcador in marcadores.keys){
                    marcador.remove()
                }
                for( data in snapshot.children){
                    val d = data.getValue(Sitio::class.java)!!
                    if(d.nombre!!.toLowerCase(Locale.getDefault()).contains(titulo.toLowerCase(Locale.getDefault()))
                            && tipos.contains(d.tipo)){
                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(d.latitud!!,d.longitud!!))
                                .title(d.nombre))
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.sitesFound(marcadores.size)
            }
        })
    }
    fun getSitesWithAllConditions(latitude: Double, longitud: Double, distancia: Int, tipos: ArrayList<String>, titulo: String, marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
        TODO()
    }

    fun getSitesTypes(){
        FirebaseDatabase.getInstance().reference.child("TiposSitio").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val list = arrayListOf<String>()
                for(snap in snapshot.children){
                    Log.d("TYPESNAP", snap.value.toString())
                    list.add(snap.value.toString())
                }
                listener!!.typesFound(list)
            }

        })
    }

    interface SiteInterface{
        fun sitesFound(size : Int)
        fun typesFound(list : ArrayList<String>)
    }
}