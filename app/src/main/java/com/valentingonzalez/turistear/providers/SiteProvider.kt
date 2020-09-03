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

class SiteProvider( private val listener : DiscoveredSites){
    private var mSiteReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Sitios")
    private var mSecretReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Secretos")
    private var mUserSecretReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("SecretosUsuario")

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
                        Log.d("Sitios",s.toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun getSiteDiscoveredSecrets(uId: String, siteId: String){
        val obtained = mutableListOf<Boolean>()
        mUserSecretReference.child(uId).child(siteId).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) { }
            override fun onDataChange(snapshot: DataSnapshot) {

                for(user_count in snapshot.children){
                    Log.d("USER_SECRET", user_count.value.toString())
                    if(user_count.value == true) {
                        obtained.add(true)
                    }else{
                        obtained.add(false)
                    }
                }
                listener.onDiscovered(obtained)
            }
        })
    }

    interface DiscoveredSites{
        fun onDiscovered(lista: List<Boolean>)
    }
}