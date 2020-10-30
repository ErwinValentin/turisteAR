package com.valentingonzalez.turistear.features.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox

import androidx.recyclerview.widget.RecyclerView

import com.valentingonzalez.turistear.R
import kotlinx.android.synthetic.main.location_type_item.view.*


class LocationTypeAdapter ( var locationTypes : ArrayList<String> , var listener: CheckSelected)  : RecyclerView.Adapter<LocationTypeAdapter.ViewHolder>(){
    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val checkBox : CheckBox = view.location_type_cb
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.location_type_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return locationTypes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val box = holder.checkBox
        box.text = locationTypes[position]
        box.setOnCheckedChangeListener { compoundButton, b ->
            listener.setSelected(compoundButton.text.toString(), b)
        }
    }

    interface CheckSelected{
        fun setSelected(text: String, selected: Boolean)
    }
}