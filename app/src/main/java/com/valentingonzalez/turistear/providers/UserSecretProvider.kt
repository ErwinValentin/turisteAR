package com.valentingonzalez.turistear.providers

import android.util.Log
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.Usuario

class UserSecretProvider (private var listener: UserSecrets){
    private var mUserSecretReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("SecretosUsuario")
    private var mUserReference = FirebaseDatabase.getInstance().reference.child("Usuarios")

    fun getSiteDiscoveredSecrets(uId: String, siteId: String){
        mUserSecretReference.child(uId).child(siteId).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) { }
            override fun onDataChange(snapshot: DataSnapshot) {
                val obtained = mutableListOf<Boolean>()
                for(user_count in snapshot.children){
                    Log.d("USER_SECRET", user_count.value.toString())
                    if(user_count.value == true) {
                        obtained.add(true)
                    }else{
                        obtained.add(false)
                    }
                }
                listener.onSiteDiscoveredStatus(obtained)
            }
        })
    }

    fun addSecretToDiscovered(uId: String, siteKey: String, secretNumber: Int){
        mUserSecretReference.child(uId).child(siteKey).child(secretNumber.toString()).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val estado = snapshot.value as Boolean
                if(!estado){
                    mUserSecretReference.child(uId).child(siteKey).child(secretNumber.toString()).setValue(true).addOnSuccessListener {
                        listener.onSecretDiscovered()
                        mUserReference.child(uId).addValueEventListener(object : ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {
                            }
                            override fun onDataChange(snapshot: DataSnapshot) {

                                var user = snapshot.getValue(Usuario::class.java)!!

                                user.puntosActuales = user.puntosActuales?.plus(10)
                                user.puntosTotales = user.puntosTotales?.plus(10)
                                if(user.puntosTotales?.rem(100)  == 0){
                                    user.nivelActual = user.nivelActual?.plus(1)
                                    mUserReference.child(uId).setValue(user)
                                }
                            }
                        })
                    }
                }
            }

        })
    }

    interface UserSecrets{
        fun onSiteDiscoveredStatus(obtained: List<Boolean>)
        fun onSecretDiscovered()
    }
}