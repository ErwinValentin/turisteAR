package com.valentingonzalez.turistear.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.adapters.ShopGridAdapter
import com.valentingonzalez.turistear.models.ShopItem
import com.valentingonzalez.turistear.models.Usuario
import com.valentingonzalez.turistear.providers.ShopProvider
import com.valentingonzalez.turistear.providers.UserProvider

class ShopFragment(var usuario: Usuario) : Fragment(), ShopGridAdapter.ShopInterface, ShopProvider.ShopProviderInterface, UserProvider.UserShopItemsListener{

    //TODO agregar nuevo fragmento para ver la lista
    lateinit var gridView : RecyclerView
    var shopAdapter : ShopGridAdapter? = null

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

    }

    override fun onItemSelected(shopItem: ShopItem) {
        if(!userItems.contains(shopItem)) {
            if (usuario.puntosActuales!! >= shopItem.precio!!) {
                Toast.makeText(context, "Comprado!", Toast.LENGTH_SHORT).show()
                userProvider.purchaseItem(shopItem)
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
    }
/*TODO create fragment
    get items from DB
    update bought items (user provider)
    add purchase dialog
    uer icons? routes? vouchers?
 */

}