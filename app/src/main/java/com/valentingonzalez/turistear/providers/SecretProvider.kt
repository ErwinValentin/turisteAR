package com.valentingonzalez.turistear.providers

import androidx.annotation.Nullable
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.Secreto

class SecretProvider (private val listener: SiteSecrets){
    private var mSiteReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Secretos")
    fun getSecrets(key: String){
        mSiteReference.child(key).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

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
//    fun getSecretKeys(key: String){
//        mSiteReference.child(key).addValueEventListener(object: ValueEventListener{
//            override fun onCancelled(error: DatabaseError) {
//            }
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val secretKeys : MutableList<String> = mutableListOf()
//                if(snapshot.exists()){
//                    for(secret in snapshot.children){
//                        val s = secret.key
//                        secretKeys.add(s!!)
//                    }
//                    listener.onSecretKeysObtained(secretKeys)
//
//                }
//            }
//        })
//    }
    interface SiteSecrets{
        fun onSecretDiscovered(secretList: List<Secreto>)
//        fun onSecretKeysObtained(keysList : List<String>)
    }
}