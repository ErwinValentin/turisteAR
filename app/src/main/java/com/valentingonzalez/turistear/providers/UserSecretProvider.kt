package com.valentingonzalez.turistear.providers

import android.util.Log
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.SitioDescubierto
import com.valentingonzalez.turistear.models.Usuario
import java.util.*
import kotlin.collections.HashMap

class UserSecretProvider (private var listener: UserSecrets){
    private var mUserSecretReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("SecretosUsuario")
    private var mUserReference = FirebaseDatabase.getInstance().reference.child("Usuarios")

    fun getSiteDiscoveredSecrets(uId: String, siteId: String){
        mUserSecretReference.child(uId).child(siteId).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) { }
            override fun onDataChange(snapshot: DataSnapshot) {
                val obtained = hashMapOf<Int, Boolean>()
                for(user_count in snapshot.children){
                    Log.d("USER_SECRET", user_count.value.toString())
                    obtained[user_count.key!!.toInt()] = user_count.value !=false
                }
                listener.onSiteDiscoveredStatus(obtained)
            }
        })
    }

    fun addSecretToDiscovered(uId: String, siteKey: String, secretNumber: Int, secretName: String){
        val sitioDescubierto = SitioDescubierto(Calendar.getInstance().time.toString(), secretName, siteKey)
        mUserSecretReference.child(uId).child(siteKey).child(secretNumber.toString()).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("UPDATE SECRETE",snapshot.value.toString())
                if(snapshot.value == false){
                    mUserSecretReference.child(uId).child(siteKey).child(secretNumber.toString()).setValue(sitioDescubierto).addOnSuccessListener {
                        listener.onSecretDiscovered()
                        mUserReference.child(uId).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {
                            }
                            override fun onDataChange(snapshot: DataSnapshot) {

                                val user = snapshot.getValue(Usuario::class.java)!!

                                user.puntosActuales = user.puntosActuales?.plus(10)
                                user.puntosTotales = user.puntosTotales?.plus(10)
                                if(user.puntosTotales?.rem(100)  == 0){
                                    user.nivelActual = user.nivelActual?.plus(1)
                                }
                                mUserReference.child(uId).setValue(user)
                            }
                        })
                    }
                }
            }

        })
    }

    interface UserSecrets{
        fun onSiteDiscoveredStatus(obtained: HashMap<Int, Boolean>)
        fun onSecretDiscovered()
    }
}