package com.valentingonzalez.turistear.providers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.valentingonzalez.turistear.models.ShopItem

class ShopProvider (var listener: ShopProviderInterface){
    var shopItemsReference = FirebaseDatabase.getInstance().reference.child("ObjetosTienda")
    var userItemsReference = FirebaseDatabase.getInstance().reference.child("Usuarios")

    fun getShopItems(){
        shopItemsReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var items = mutableListOf<ShopItem>()
                for(data in snapshot.children){
                    val item = data.getValue(ShopItem::class.java)
                    item?.let { items.add(it) }
                }
                listener.onShopItemsObtained(items)
            }
        })
    }
    fun getUserItems(){
        val uId = FirebaseAuth.getInstance().uid.toString()
        userItemsReference.child(uId).child("objetos").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ShopItem>()
                for(data in snapshot.children){
                    val item = data.getValue(ShopItem::class.java)
                    item?.let { items.add(it) }
                }
                listener.onUserItemsObtained(items)
            }
        })
    }
    interface ShopProviderInterface{
        fun onShopItemsObtained(items: List<ShopItem>)
        fun onUserItemsObtained(items: List<ShopItem>)
    }
}