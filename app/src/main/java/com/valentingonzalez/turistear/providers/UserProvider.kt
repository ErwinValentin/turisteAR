package com.valentingonzalez.turistear.providers

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.FavoritoUsuario
import com.valentingonzalez.turistear.models.Usuario
import java.util.*

class UserProvider(private var listener : UserProviderListener) {
    var mUserReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Usuarios")
    fun createUser(usuario: Usuario): Task<Void> {
        val map: MutableMap<String, Any?> = HashMap()
        map["name"] = usuario.nombre
        map["email"] = usuario.email
        return mUserReference.child(usuario.id!!).setValue(map)
    }

    fun getUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        mUserReference.child(currentUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u = snapshot.getValue(Usuario::class.java)!!
                Log.d("Usuario", u.toString())
                listener.getUserName(u)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    fun addFavorite(favorito : FavoritoUsuario){
        //val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("favoritos").push().setValue(favorito)
    }
    fun removeFav(location: String, numeroSecreto: Int){
        mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("favoritos")
                .orderByChild("llave").equalTo(location).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
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
//        Log.d("TEST",FirebaseAuth.getInstance().uid.toString())
        mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("favoritos")
                .orderByChild("llave").equalTo(key).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
//                        Log.d("TEST",snapshot.toString())
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
    interface UserProviderListener {
        fun onFavoriteChecked(isFav : List<Boolean>)
        fun getUserName(user: Usuario)
    }
}