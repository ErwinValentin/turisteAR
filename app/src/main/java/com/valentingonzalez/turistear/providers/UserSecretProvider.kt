package com.valentingonzalez.turistear.providers

import android.util.Log
import com.google.firebase.database.*

class UserSecretProvider (private var listener: UserSecrets){
    private var mUserSecretReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("SecretosUsuario")

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
        mUserSecretReference.child(uId).child(siteKey).child(secretNumber.toString()).setValue(true).addOnSuccessListener {
            listener.onSecretDiscovered()
        }
    }

    interface UserSecrets{
        fun onSiteDiscoveredStatus(obtained: List<Boolean>)
        fun onSecretDiscovered()
    }
}