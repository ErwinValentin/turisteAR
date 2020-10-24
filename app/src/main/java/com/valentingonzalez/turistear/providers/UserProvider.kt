package com.valentingonzalez.turistear.providers

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.FavoritoUsuario
import com.valentingonzalez.turistear.models.ShopItem
import com.valentingonzalez.turistear.models.Usuario
import java.util.*

class UserProvider {
    private var listener: Any
    constructor(listener: UserProviderListener){
        this.listener = listener
    }
    constructor(listener : UserShopItemsListener){
        this.listener = listener
    }
    var mUserReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Usuarios")
    fun createUser(usuario: Usuario): Task<Void> {
        val map: MutableMap<String, Any?> = HashMap()
        map["nombre"] = usuario.nombre
        map["email"] = usuario.email
        map["puntosActuales"] = 0
        map["puntosTotales"] = 0
        map["nivelActual"] = 1
        return mUserReference.child(usuario.id!!).setValue(map)
    }

    fun getUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        mUserReference.child(currentUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u = snapshot.getValue(Usuario::class.java)!!
                Log.d("Usuario", u.toString())
                if(listener is UserProviderListener){
                    (listener as UserProviderListener).getUser(u)
                }else{
                    (listener as UserShopItemsListener).getUser(u)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    fun addFavorite(favorito : FavoritoUsuario){
        //val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("favoritos").push().setValue(favorito)
    }
    fun removeFav(location: String?, numeroSecreto: Int?){
        mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("favoritos")
                .orderByChild("llave").equalTo(location).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(fav in snapshot.children){
                            val f = fav.getValue(FavoritoUsuario::class.java)
                            val num = f?.numSecreto!!
                            if(numeroSecreto  == num) {
                                mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("favoritos").child(fav.key!!).removeValue()
                            }
                        }
                    }
                })
    }

    fun getFavorites(){
        mUserReference.child(FirebaseAuth.getInstance().uid.toString()).child("favoritos")
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val favoritos = mutableListOf<FavoritoUsuario>()
                        for(fav in snapshot.children){
                            fav.getValue(FavoritoUsuario::class.java)?.let { favoritos.add(it) }
                        }
                        (listener as UserProviderListener).getAllFavorites(favoritos)
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
                        (listener as UserProviderListener).onFavoriteChecked(favorites)
                    }

                })
    }
    fun purchaseItem(shopItem: ShopItem){
        val reference = mUserReference.child(FirebaseAuth.getInstance().uid.toString())
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                if(usuario != null){
                    usuario.puntosActuales = usuario.puntosActuales!!.minus(shopItem.precio!!.toInt())
                    reference.setValue(usuario)
                    reference.child("objetos").push().setValue(shopItem)
                    (listener as UserShopItemsListener).itemPurchased(shopItem)
                }
            }

        })
    }
    interface UserProviderListener {
        fun onFavoriteChecked(isFav : List<Boolean>)
        fun getUser(user: Usuario)
        fun getAllFavorites(favoritos: List<FavoritoUsuario>)
    }
    interface UserShopItemsListener{
        fun itemPurchased(shopItem: ShopItem)
        fun getUser(user: Usuario)
    }
}