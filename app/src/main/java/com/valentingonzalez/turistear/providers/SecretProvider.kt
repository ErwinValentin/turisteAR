package com.valentingonzalez.turistear.providers

import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.Secreto

class SecretProvider (private val listener: SiteSecrets){
    private var mSiteReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Secretos")
    fun getSecrets(key: String){
        mSiteReference.child(key).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val secretList : MutableList<Secreto> = mutableListOf()
                if(snapshot.exists()){
                    for(secret in snapshot.children){
                        val s = secret.getValue(Secreto::class.java)
                        secretList.add(s!!)
                    }
                    listener.onSecretDiscovered(secretList)
                }
            }
        })
    }
    interface SiteSecrets{
        fun onSecretDiscovered(secretList: List<Secreto>)
    }
}