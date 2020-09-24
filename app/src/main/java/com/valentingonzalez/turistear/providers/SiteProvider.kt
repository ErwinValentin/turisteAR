package com.valentingonzalez.turistear.providers

import android.renderscript.Sampler
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
import com.valentingonzalez.turistear.models.SitioDescubierto
import com.valentingonzalez.turistear.models.Usuario
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("UNUSED_PARAMETER")
class SiteProvider(@Nullable var listener: SiteInterface?){
    private var mSiteReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Sitios")
    private var mSecretReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Secretos")
    private var mUserReference = FirebaseDatabase.getInstance().reference.child("Usuarios")
    private var mUserSecretReference = FirebaseDatabase.getInstance().reference.child("SecretosUsuario")
    private var mDiscoveredSiteReference = FirebaseDatabase.getInstance().reference.child("DiscoveredSites")

    fun createSite(sitio: Sitio, secrets: List<Secreto>): Task<Void> {
        val key = mSiteReference.push().key!!
        mSecretReference.child(key).setValue(secrets)
        return mSiteReference.child(key).setValue(sitio)
    }
    fun getSite(key: String){
        mSiteReference.child(key).addListenerForSingleValueEvent( object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val sitio = snapshot.getValue(Sitio::class.java)
                Log.d("SITIO", sitio.toString())
                listener!!.getSingleSite(sitio!!, key)
            }

        })
    }
    fun getSites(latitud: Double, longitud: Double, distancia: Int, tipos: ArrayList<String>, titleContains: String,markers: HashMap<Marker, Sitio>, map: GoogleMap, all: Boolean ){
        if(all || (distancia== 0 && tipos.size == 0 && titleContains.isEmpty())){
            getAllSites(markers, map)
            Log.d("SITEPROVIDER", "CALLED getAll")
        }else{
            if(distancia!= 0 && tipos.size == 0 && titleContains.isEmpty()){
                getNearbySites(latitud, longitud, distancia ,markers, map)
                Log.d("SITEPROVIDER", "CALLED getNearby")
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
                                .title(s.nombre)
                                .snippet(s.tipo))
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
        /**
         * distancia = 100, es 10 km o 10,000 m
         * distancia = 10 es 1 km o 1000 metros
         */
        val addedDistance = distancia * 0.00089
        mSiteReference.orderByChild("latitud").startAt(latitude-addedDistance).endAt(latitude+addedDistance).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for(marcador in marcadores.keys){
                    marcador.remove()
                }

                for(data in snapshot.children){
                    val d:Sitio = data.getValue(Sitio::class.java)!!
                    if(d.longitud!! <= longitud+addedDistance && d.longitud!!>=longitud-addedDistance){
                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(d.latitud!!,d.longitud!!))
                                .title(d.nombre)
                                .snippet(d.tipo))

                        Log.d("ENTRE DISTANCIA", ""+(latitude-addedDistance)+" "+d.latitud+" "+(latitude+addedDistance))
                        val between = (latitude-addedDistance) < d.latitud!!
                        val after = d.latitud!! < (latitude+addedDistance)
                        Log.d("ENTRE DISTANCIA", (between && after).toString())
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.listReady()
            }
        })

    }

    fun getSitesByType(latitude: Double, longitud: Double, tipos: ArrayList<String>, marcadores: HashMap<Marker, Sitio>, map: GoogleMap){

        mSiteReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError){

            }
            //

            override fun onDataChange(snapshot: DataSnapshot) {
                for(marcador in marcadores.keys){
                    marcador.remove()
                }
                for( data in snapshot.children){
                    val d = data.getValue(Sitio::class.java)!!
                    if(tipos.contains(d.tipo)){
                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(d.latitud!!,d.longitud!!))
                                .title(d.nombre)
                                .snippet(d.tipo))
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.listReady()
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
                                .title(d.nombre)
                                .snippet(d.tipo))
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.listReady()
            }
        })
    }
    fun getSitesByDistanceAndType(latitude: Double, longitud: Double, distancia: Int, tipos: ArrayList<String>, marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
        val addedDistance = distancia * 0.00089
        mSiteReference.orderByChild("latitud").startAt(latitude-addedDistance).endAt(latitude+addedDistance).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for(marcador in marcadores.keys){
                    marcador.remove()
                }

                for(data in snapshot.children){
                    val d:Sitio = data.getValue(Sitio::class.java)!!
                    if(d.longitud!! <= longitud+addedDistance && d.longitud!!>=longitud-addedDistance && tipos.contains(d.tipo)){
                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(d.latitud!!,d.longitud!!))
                                .title(d.nombre)
                                .snippet(d.tipo))

                        Log.d("ENTRE DISTANCIA", ""+(latitude-addedDistance)+" "+d.latitud+" "+(latitude+addedDistance))
                        val between = (latitude-addedDistance) < d.latitud!!
                        val after = d.latitud!! < (latitude+addedDistance)
                        Log.d("ENTRE DISTANCIA", (between && after).toString())
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.listReady()
            }
        })
    }
    fun getSitesByDistanceAndTitle(latitude: Double, longitud: Double, distancia: Int, titulo: String , marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
        val addedDistance = distancia * 0.00089
        mSiteReference.orderByChild("latitud").startAt(latitude-addedDistance).endAt(latitude+addedDistance).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for(marcador in marcadores.keys){
                    marcador.remove()
                }

                for(data in snapshot.children){
                    val d:Sitio = data.getValue(Sitio::class.java)!!
                    if(d.longitud!! <= longitud+addedDistance && d.longitud!!>=longitud-addedDistance && d.nombre!!.toLowerCase(Locale.getDefault()).contains(titulo.toLowerCase(Locale.getDefault()))){
                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(d.latitud!!,d.longitud!!))
                                .title(d.nombre)
                                .snippet(d.tipo))

                        Log.d("ENTRE DISTANCIA", ""+(latitude-addedDistance)+" "+d.latitud+" "+(latitude+addedDistance))
                        val between = (latitude-addedDistance) < d.latitud!!
                        val after = d.latitud!! < (latitude+addedDistance)
                        Log.d("ENTRE DISTANCIA", (between && after).toString())
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.listReady()
            }
        })
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
                                .title(d.nombre)
                                .snippet(d.tipo))
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.listReady()
            }
        })
    }
    fun getSitesWithAllConditions(latitude: Double, longitud: Double, distancia: Int, tipos: ArrayList<String>, titulo: String, marcadores: HashMap<Marker, Sitio>, map: GoogleMap) {
        val addedDistance = distancia * 0.00089
        mSiteReference.orderByChild("latitud").startAt(latitude-addedDistance).endAt(latitude+addedDistance).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for(marcador in marcadores.keys){
                    marcador.remove()
                }

                for(data in snapshot.children){
                    val d:Sitio = data.getValue(Sitio::class.java)!!
                    if(d.longitud!! <= longitud+addedDistance && d.longitud!!>=longitud-addedDistance
                            && d.nombre!!.toLowerCase(Locale.getDefault())
                                    .contains(titulo.toLowerCase(Locale.getDefault()))
                            && tipos.contains(d.tipo)
                    ){
                        val marker = map.addMarker(MarkerOptions()
                                .position(LatLng(d.latitud!!,d.longitud!!))
                                .title(d.nombre)
                                .snippet(d.tipo))

                        Log.d("ENTRE DISTANCIA", ""+(latitude-addedDistance)+" "+d.latitud+" "+(latitude+addedDistance))
                        val between = (latitude-addedDistance) < d.latitud!!
                        val after = d.latitud!! < (latitude+addedDistance)
                        Log.d("ENTRE DISTANCIA", (between && after).toString())
                        marker.tag = data.key
                        marcadores[marker] = d
                    }
                }
                listener!!.listReady()
            }
        })
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

    fun addSiteToDiscovered(siteId: String, uId: String, nombre: String, fecha: String, esSitio: Boolean){
        val sitioDescubierto = SitioDescubierto(fecha, nombre, siteId)
        Log.d("USERID", uId)

        mDiscoveredSiteReference.child(uId).child(siteId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == null){
                    val siteSecrets = arrayListOf<Boolean>(false, false, false)
                    mUserSecretReference.child(uId).child(siteId).setValue(siteSecrets)
                    mDiscoveredSiteReference.child(uId).child(siteId).setValue(sitioDescubierto)
                    Log.d("EXITO", "exito al agregar un sitio")
                    mUserReference.child(uId).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var user = snapshot.getValue(Usuario::class.java)!!
                            Log.d("PUNTOS", "agregando puntos al usuario ${user.nombre}")
                            user.puntosActuales = user.puntosActuales?.plus(15)
                            user.puntosTotales = user.puntosTotales?.plus(15)
                            if(user.puntosTotales?.rem(100)  == 0){
                                user.nivelActual = user.nivelActual?.plus(1)
                            }
                            mUserReference.child(uId).setValue(user)
                        }
                    })
                }
            }
        })
    }

    fun getDiscoveredSites(uId: String){
        mDiscoveredSiteReference.child(uId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<SitioDescubierto>()
                Log.d("DESCUBIERTOS", snapshot.toString())
                for(sitio in snapshot.children){
                    val sitioDescubierto: SitioDescubierto = sitio.getValue(SitioDescubierto::class.java)!!
                    lista.add(sitioDescubierto)
                }

                listener!!.userDiscovered(lista)
            }
        })
    }

    interface SiteInterface{
        fun userDiscovered(list: List<SitioDescubierto>)
        fun listReady()
        fun typesFound(list : ArrayList<String>)
        fun getSingleSite(site: Sitio, key: String)
    }
}


