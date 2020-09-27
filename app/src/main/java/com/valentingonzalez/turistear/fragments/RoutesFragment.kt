package com.valentingonzalez.turistear.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.adapters.RouteAdapter
import com.valentingonzalez.turistear.models.Ruta
import com.valentingonzalez.turistear.providers.RoutesProvider

class RoutesFragment: Fragment(), RoutesProvider.RoutesInterface, RouteAdapter.RouteActions {

    private var routesListener : RouteFragmentInterface? = null
    private var routeProvider = RoutesProvider(this)
    private lateinit var routeAdapter: RouteAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            routesListener = context as RouteFragmentInterface
        }catch(e: ClassCastException){
            throw  java.lang.ClassCastException("$activity debe implementar la interfaz")
        }
    }

    override fun onResume() {
        super.onResume()
        getRoutes()
    }

    private fun getRoutes() {
        routeProvider.getRutas()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_list_layout, container, false)
        recyclerView = root.findViewById(R.id.recycler_view_item)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun getRoutes(rutas: List<Ruta>) {
        routeAdapter = RouteAdapter(rutas, this, context!! )
        recyclerView.adapter = routeAdapter
        (recyclerView.adapter as RouteAdapter).notifyDataSetChanged()
    }

    override fun selectRoute(ruta: Ruta) {
        routesListener!!.routeSelected(ruta)
    }

    public interface RouteFragmentInterface{
        fun routeSelected(ruta: Ruta)
    }
}