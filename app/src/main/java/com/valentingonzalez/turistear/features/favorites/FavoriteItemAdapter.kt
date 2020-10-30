package com.valentingonzalez.turistear.features.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.FavoritoUsuario

class FavoriteItemAdapter ( var favorites : List<FavoritoUsuario> , var listener: FavoriteActions)  : RecyclerView.Adapter<FavoriteItemAdapter.ViewHolder>(){
    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val favoriteType : ImageView = view.findViewById(R.id.favorite_item_type)
        val favoriteTitle : TextView = view.findViewById(R.id.favorite_item_title)
        val favoriteDate : TextView  = view.findViewById(R.id.favorite_item_date)
        val locationButton : ImageButton = view.findViewById(R.id.goto_location_button)
        val favoriteButton : ImageButton = view.findViewById(R.id.favorite_button)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fav = favorites[position]
        if(fav.numSecreto == -1){
            holder.favoriteType.setImageResource(R.drawable.ic_location_on_white_24dp)
        }else{
            holder.favoriteType.setImageResource(R.drawable.open_chest_white_w_bg)
        }
        holder.favoriteTitle.text = fav.nombre
        holder.favoriteDate.text = fav.fechaAgregado
        holder.locationButton.setOnClickListener{
            listener.gotoLocation(fav)
        }
        holder.favoriteButton.tag = 0 // Is favorite
        holder.favoriteButton.setOnClickListener {
            if(it.tag == 0) {
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                listener.changeFavorite(fav, true)
                it.tag = 1
            }
            else if(it.tag == 1){
                listener.changeFavorite(fav, false)
                it.tag = 0
                holder.favoriteButton.setImageResource(R.drawable.ic_favorite_red_400_24dp)
            }
        }
    }

    interface FavoriteActions{
        fun changeFavorite(favorito: FavoritoUsuario, remove: Boolean)
        fun gotoLocation(favorito: FavoritoUsuario)
    }
}