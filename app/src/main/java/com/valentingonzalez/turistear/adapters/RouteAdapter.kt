package com.valentingonzalez.turistear.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.Ruta

class RouteAdapter (var rutas : List<Ruta>, var listener: RouteActions, var context: Context)  : RecyclerView.Adapter<RouteAdapter.ViewHolder>(){

    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val nombreRuta : TextView = view.findViewById(R.id.route_title)
        val puntosRuta : LinearLayout = view.findViewById(R.id.route_points)
        val item  = view
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.route_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return rutas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ruta = rutas[position]
        for(punto in ruta.puntos!!){
            val view = TextView(context)
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            view.text = punto.nombre
            holder.puntosRuta.addView(view)
        }
        holder.nombreRuta.text = ruta.nombre
        holder.itemView.setOnClickListener {
            listener.selectRoute(ruta)
        }
    }

    interface RouteActions{
        fun selectRoute(ruta: Ruta)
    }
}