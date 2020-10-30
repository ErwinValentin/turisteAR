package com.valentingonzalez.turistear.providers

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.valentingonzalez.turistear.models.Ruta

class RoutesProvider (private val listener : RoutesInterface){

    private var mRoutesReference = FirebaseDatabase.getInstance().reference.child("Rutas")

    fun getRutas(){
        mRoutesReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val rutas = mutableListOf<Ruta>()
                for (data in snapshot.children){
                    data.getValue(Ruta::class.java).let {  rutas.add(it!!) }
                }
                listener.getRoutes(rutas)
            }

        })
    }

    interface RoutesInterface{
        fun getRoutes(rutas: List<Ruta>)
    }
}