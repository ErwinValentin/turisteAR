package com.valentingonzalez.turistear.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.adapters.ShopGridAdapter
import com.valentingonzalez.turistear.models.ShopItem
import com.valentingonzalez.turistear.models.Usuario
import com.valentingonzalez.turistear.providers.ShopProvider
import com.valentingonzalez.turistear.providers.UserProvider

class ShopFragment : Fragment(), ShopGridAdapter.ShopInterface, ShopProvider.ShopProviderInterface, UserProvider.UserShopItemsListener{

    //TODO agregar nuevo fragmento para ver la lista
    lateinit var gridView : RecyclerView
    var shopAdapter : ShopGridAdapter? = null
    lateinit var usuario : Usuario
    var shopItems = mutableListOf<ShopItem>()
    var userItems = mutableListOf<ShopItem>()
    private lateinit var shopProvider: ShopProvider
    private lateinit var userProvider: UserProvider

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_list_layout, container, false)
        gridView = root.findViewById(R.id.recycler_view_item)
        return root
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userProvider = UserProvider(this)
        shopProvider = ShopProvider(this)
        shopProvider.getShopItems()
        userProvider.getUser()
    }

    override fun onItemSelected(shopItem: ShopItem) {
        if(!userItems.contains(shopItem)) {
            if (usuario.puntosActuales!! >= shopItem.precio!!) {
                //TODO dialogo de confimacion

                val dialogBuilder = AlertDialog.Builder(context)
                val inflater = requireActivity().layoutInflater

                val contentView = inflater.inflate(R.layout.shop_confirm_purchase_dialog, null)

                val itemDescription : TextView = contentView.findViewById(R.id.shop_item_description)
                val itemPrice : TextView = contentView.findViewById(R.id.shop_item_price)
                val userCurrency : TextView = contentView.findViewById(R.id.user_current_balance)
                val priceImageView : ImageView = contentView.findViewById(R.id.shop_item_image)

                itemDescription.text = shopItem.descripcion
                itemPrice.text = shopItem.precio.toString()
                val dlPath = FirebaseStorage.getInstance().reference.child(shopItem.imagen!!)
                dlPath.downloadUrl.addOnSuccessListener {
                    Picasso.get()
                            .load(it)
                            .placeholder(R.drawable.coin_icon)
                            .into(priceImageView)
                }
                userCurrency.text = (usuario.puntosActuales!! - shopItem.precio!!).toString()
                dialogBuilder.setView(contentView)
                dialogBuilder.setPositiveButton("Comprar"){ _ , _ ->
                            Toast.makeText(context, "Comprado!", Toast.LENGTH_SHORT).show()
                            userProvider.purchaseItem(shopItem)
                        }.setNegativeButton("Cancelar"){ dialog , _ ->
                            dialog.cancel()
                        }
                dialogBuilder.create().show()

            } else {
                Toast.makeText(context, "No se puede comprar!", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(context, "Ya tienes este objeto!", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onShopItemsObtained(items: List<ShopItem>) {
        shopItems.clear()
        shopItems.addAll(items)
        shopProvider.getUserItems()
    }

    override fun onUserItemsObtained(items: List<ShopItem>) {
        userItems.clear()
        userItems.addAll(items)

        shopAdapter = ShopGridAdapter(shopItems,userItems,this, FirebaseStorage.getInstance())
        gridView.adapter = shopAdapter
        gridView.layoutManager = GridLayoutManager(context, 3)
    }

    override fun itemPurchased(shopItem: ShopItem) {
        userItems.add(shopItem)
        shopAdapter!!.userItems = userItems
        shopAdapter!!.notifyDataSetChanged()
    }

    override fun getUser(user: Usuario) {
        usuario = user
    }
}