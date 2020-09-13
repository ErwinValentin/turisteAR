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
                        lista.put(marker,s)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun getNearbySites(latitude: Double, longitud: Double, marcadores: HashMap<Marker, Sitio>, map: GoogleMap){
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
                        marcadores.put(marker, d)
                    }

                }

            }

        })
    }
//    interface DiscoveredSites{
//        fun onDiscovered(lista: List<Boolean>)
//    }
}