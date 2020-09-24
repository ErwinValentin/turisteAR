package com.valentingonzalez.turistear.activities.maps

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.adapters.LocationTypeAdapter
import com.valentingonzalez.turistear.includes.BasicToolbar
import com.valentingonzalez.turistear.models.Sitio
import com.valentingonzalez.turistear.models.SitioDescubierto
import com.valentingonzalez.turistear.providers.SiteProvider
import kotlinx.android.synthetic.main.search_options_layout.*
import java.util.ArrayList


class SearchOptionsActivity: AppCompatActivity(), LocationTypeAdapter.CheckSelected, SiteProvider.SiteInterface{
    var selectedTypes: ArrayList<String> = arrayListOf()
    val provider = SiteProvider(this)
    val allTypes = arrayListOf<String>()
    lateinit var adapter : LocationTypeAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_options_layout)
        search_confirm_button.setOnClickListener{
            val intent = Intent()
            intent.putExtra("DISTANCE", search_distance_bar.progress)
            intent.putStringArrayListExtra("TYPES",selectedTypes)
            intent.putExtra("CONTAINS",search_text_tiet.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        provider.getSitesTypes()
        adapter = LocationTypeAdapter(allTypes, this)
        val recyclerView = findViewById<RecyclerView>(R.id.location_types_rv)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        BasicToolbar.show(this, "Opciones de busqueda", true)
        search_distance_bar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p1 < 10){
                    search_distance_value.text = "Distancia : ${p1 * 100} m"
                }else{
                    search_distance_value.text = String.format("Distancia %.2f km", (p1* 0.1))
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    override fun setSelected(text: String, selected: Boolean) {
        if(selected){
            selectedTypes.add(text)
        }else{
            selectedTypes.remove(text)
        }
    }

    override fun userDiscovered(list: List<SitioDescubierto>) {
    }

    override fun listReady() {
    }

    override fun typesFound(list: ArrayList<String>) {
        allTypes.clear()
        allTypes.addAll(list)
        adapter.notifyDataSetChanged()
    }

    override fun getSingleSite(site: Sitio, key: String) {
    }

}