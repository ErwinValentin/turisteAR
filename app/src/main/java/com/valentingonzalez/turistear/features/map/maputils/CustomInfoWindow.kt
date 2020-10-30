package com.valentingonzalez.turistear.features.map.maputils

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.valentingonzalez.turistear.R

class CustomInfoWindow(private val layoutInflater: LayoutInflater) : GoogleMap.InfoWindowAdapter{

    override fun getInfoContents(marker: Marker?): View {
        val view = layoutInflater.inflate(R.layout.custom_info_window,null)
        val snippet = marker!!.snippet
        view.findViewById<TextView>(R.id.marker_title).text = marker.title
        view.findViewById<TextView>(R.id.marker_snippet).text = snippet
        val icon = view.findViewById<ImageView>(R.id.marker_icon)
        when(snippet){
            "Museo"->{
                icon.setImageResource(R.drawable.marker_museum_icon)
            }
            "Parque de Diversiones"->{
                icon.setImageResource(R.drawable.marker_amusement_icon)
            }
            "Parque Natural"->{
                icon.setImageResource(R.drawable.marker_park_icon)
            }
            "Zoologico"->{
                icon.setImageResource(R.drawable.marker_zoo_icon)
            }
            "Sitio Historico"->{
                icon.setImageResource(R.drawable.marker_historical_icon)
            }
            "Restaurante"->{
                icon.setImageResource(R.drawable.marker_dining_icon)
            }
            "Rio"->{
                icon.setImageResource(R.drawable.marker_river_icon)
            }
            "Lago"->{
                icon.setImageResource(R.drawable.marker_lake_icon)
            }
            "Montaña"->{
                icon.setImageResource(R.drawable.marker_mountain_icon)
            }
            "Centro Comercial"->{
                icon.setImageResource(R.drawable.marker_mall_icon)
            }
        }
        return view
    }

    override fun getInfoWindow(p0: Marker?): View? {
        return null
    }

}