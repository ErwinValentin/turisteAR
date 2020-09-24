package com.valentingonzalez.turistear.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.SitioDescubierto

class VisitedAdapter ( var visited : List<SitioDescubierto> , var listener: VisitedActions)  : RecyclerView.Adapter<VisitedAdapter.ViewHolder>(){
    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val favoriteType : ImageView = view.findViewById(R.id.favorite_item_type)
        val favoriteTitle : TextView = view.findViewById(R.id.favorite_item_title)
        val favoriteDate: TextView = view.findViewById(R.id.favorite_item_date)
        val locationButton : ImageButton = view.findViewById(R.id.goto_location_button)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.discovered_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return visited.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vis = visited[position]
        holder.favoriteType.setImageResource(R.drawable.ic_location_on_white_24dp)

        holder.favoriteTitle.text = vis.nombre
        holder.favoriteDate.text = vis.fecha
        holder.locationButton.setOnClickListener{
            listener.gotoLocation(vis)
        }
    }

    interface VisitedActions{
        fun gotoLocation(sitio: SitioDescubierto)
    }
}