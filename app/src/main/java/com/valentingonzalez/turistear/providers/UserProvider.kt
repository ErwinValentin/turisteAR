package com.valentingonzalez.turistear.providers

import android.util.Log
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.Usuario
import java.util.*

class UserProvider {
    var mFirebaseDatabase: DatabaseReference
    fun createUser(usuario: Usuario): Task<Void> {
        val map: MutableMap<String, Any?> = HashMap()
        map["name"] = usuario.nombre
        map["email"] = usuario.email
        return mFirebaseDatabase.child(usuario.id!!).setValue(map)
    }

    fun getUser(nameView: TextView) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        Log.d("PROVIDER", currentUser)
        mFirebaseDatabase.child(currentUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u = snapshot.getValue(Usuario::class.java)!!
                nameView.text = u.nombre
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    init {
        mFirebaseDatabase = FirebaseDatabase.getInstance().reference.child("Usuarios")
    }
}