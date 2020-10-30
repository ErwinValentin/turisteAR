package com.valentingonzalez.turistear.features.visited

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.Sitio
import com.valentingonzalez.turistear.models.SitioDescubierto
import com.valentingonzalez.turistear.providers.SiteProvider

class VisitedFragment (var uId: String): Fragment(), SiteProvider.SiteInterface, VisitedAdapter.VisitedActions {

    private var visitedListener : VisitedFragmentInterface? = null
    private var siteProvider = SiteProvider(this)
    private lateinit var visitedAdapter: VisitedAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            visitedListener = context as VisitedFragmentInterface
        }catch(e: ClassCastException){
            throw  java.lang.ClassCastException("$activity debe implementar la interfaz")
        }
    }

    override fun onResume() {
        super.onResume()
        loadVisited()
    }

    private fun loadVisited() {
        siteProvider.getDiscoveredSites(uId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_list_layout, container, false)
        recyclerView = root.findViewById(R.id.recycler_view_item)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }
    interface VisitedFragmentInterface{
        fun gotoVisited(sitio: SitioDescubierto)
    }

    override fun userDiscovered(list: List<SitioDescubierto>) {
        visitedAdapter = VisitedAdapter(list, this)
        recyclerView.adapter = visitedAdapter
        (recyclerView.adapter as VisitedAdapter).notifyDataSetChanged()
    }

    override fun listReady() {
    }

    override fun typesFound(list: ArrayList<String>) {
    }

    override fun getSingleSite(site: Sitio, key: String) {
    }

    override fun gotoLocation(sitio: SitioDescubierto) {
        visitedListener!!.gotoVisited(sitio)
    }

}