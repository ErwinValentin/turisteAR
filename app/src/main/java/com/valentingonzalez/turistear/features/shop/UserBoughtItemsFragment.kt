package com.valentingonzalez.turistear.features.shop

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.ShopItem
import com.valentingonzalez.turistear.models.Usuario
import com.valentingonzalez.turistear.providers.ShopProvider
import com.valentingonzalez.turistear.providers.UserProvider

class UserBoughtItemsFragment : Fragment(), UserItemsGridAdapter.UserItemsInterface, ShopProvider.ShopProviderInterface, UserProvider.UserShopItemsListener{

    lateinit var gridView : RecyclerView
    var itemsAdapter : UserItemsGridAdapter? = null
    lateinit var usuario : Usuario
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
        shopProvider.getUserItems()
        userProvider.getUser()
    }

    override fun onItemSelected(shopItem: ShopItem) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage("¿Desea usar el objeto ${shopItem.nombre}?")
        dialogBuilder.setTitle("Usar Objeto")
        dialogBuilder.setPositiveButton("Usar"){ _ , _ ->
            userProvider.useItem(shopItem)
            itemsAdapter!!.userItems.remove(shopItem)
            gridView.adapter?.notifyDataSetChanged()
        }.setNegativeButton("Cancelar"){ dialog , _ ->
            dialog.cancel()
        }
        dialogBuilder.create().show()
    }
    override fun onShopItemsObtained(items: List<ShopItem>) {
    }
    override fun onUserItemsObtained(items: List<ShopItem>) {
        userItems.clear()
        userItems.addAll(items)
        Log.d("USUARIO_OBJETOS", userItems.toString())
        itemsAdapter = UserItemsGridAdapter(userItems, this, FirebaseStorage.getInstance())
        gridView.adapter = itemsAdapter
        gridView.layoutManager = GridLayoutManager(context, 3)
        gridView.adapter?.notifyDataSetChanged()
    }

    override fun itemPurchased(shopItem: ShopItem) {

    }

    override fun getUser(user: Usuario) {
        usuario = user
    }
}