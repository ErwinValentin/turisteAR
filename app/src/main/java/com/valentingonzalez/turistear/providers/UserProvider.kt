package com.valentingonzalez.turistear.providers

import android.renderscript.Sampler
import android.util.Log
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.FavoritoUsuario
import com.valentingonzalez.turistear.models.Usuario
import java.util.*

class UserProvider(private var listener : FavoriteCheck) {
    var mUserReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Usuarios")
    fun createUser(usuario: Usuario): Task<Void> {
        val map: MutableMap<String, Any?> = HashMap()
        map["name"] = usuario.nombre
        map["email"] = usuario.email
        return mUserReference.child(usuario.id!!).setValue(map)
    }

    fun getUser(nameView: TextView) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        Log.d("PROVIDER", currentUser)
        mUserReference.child(currentUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u = snapshot.getValue(Usuario::class.java)!!
                nameView.text = u.nombre
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    fun addFavorite(favorito : FavoritoUsuario){
        //val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("Favoritos").push().setValue(favorito)
    }
    fun removeFav(location: String, numeroSecreto: Int){
        mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("Favoritos")
                .orderByChild("llave").equalTo(location).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(fav in snapshot.children){
                            val f = fav.getValue(FavoritoUsuario::class.java)
                            val num = f?.numSecreto!!
                            if(numeroSecreto  == num) {
                                mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("Favoritos").child(fav.key!!).removeValue()
                            }
                        }
                    }
                })
    }
    fun isFavorite(key: String, numeroSecreto: List<Int>){
        Log.d("TEST",FirebaseAuth.getInstance().uid.toString())
        mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("Favoritos")
                .orderByChild("llave").equalTo(key).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("TEST",snapshot.toString())
                        var favorites = mutableListOf(false)
                        if(numeroSecreto[0]!=-1){
                            favorites = mutableListOf(false, false, false)
                        }
                        for(fav in snapshot.children){
                            val f = fav.getValue(FavoritoUsuario::class.java)
                            val num = f?.numSecreto!!
                            if(numeroSecreto[0]==-1){
                                if(numeroSecreto.contains(num)){
                                    favorites[0]= true
                                }
                            }else{
                                if(numeroSecreto.contains(num)){
                                    favorites[num] = true
                                }
                            }
                        }
                        listener.onFavoriteChecked(favorites)
                    }

                })
    }

    interface FavoriteCheck{
        fun onFavoriteChecked(isFav : List<Boolean>)
    }
}