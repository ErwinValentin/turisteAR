package com.valentingonzalez.turistear.features.shop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.ShopItem

class UserItemsGridAdapter (var userItems : MutableList<ShopItem>, val listener: UserItemsInterface, val storage: FirebaseStorage) : Adapter<UserItemsGridAdapter.ViewHolder>(){

    class ViewHolder (var view: View): RecyclerView.ViewHolder(view){
        var card : CardView = view.findViewById(R.id.shop_item_card)
        var title : TextView = view.findViewById(R.id.shop_item_title)
        var price : TextView = view.findViewById(R.id.shop_item_price)
        var icon : ImageView = view.findViewById(R.id.shop_item_image)
        val layout : LinearLayout = view.findViewById(R.id.item_layout)
        var priceLayout : LinearLayout = view.findViewById(R.id.price_linear_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.shop_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return userItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.priceLayout.visibility = View.GONE
        val currentItem = userItems[position]
        holder.layout.setOnClickListener{
            listener.onItemSelected(currentItem)
        }
        storage.reference.child(currentItem.imagen!!).downloadUrl.addOnSuccessListener {
            Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.coin_icon)
                    .into(holder.icon)
        }

        holder.title.text = currentItem.nombre +"\n"+ currentItem.descripcion
        holder.price.text = currentItem.precio.toString()
    }

    interface UserItemsInterface{
        fun onItemSelected(shopItem: ShopItem)
    }
}