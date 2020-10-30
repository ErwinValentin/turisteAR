package com.valentingonzalez.turistear.features.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.FavoritoUsuario
import com.valentingonzalez.turistear.models.Sitio
import com.valentingonzalez.turistear.models.SitioDescubierto
import com.valentingonzalez.turistear.models.Usuario
import com.valentingonzalez.turistear.providers.SiteProvider
import com.valentingonzalez.turistear.providers.UserProvider

class UserFavoritesFragment : Fragment(), SiteProvider.SiteInterface, FavoriteItemAdapter.FavoriteActions, UserProvider.UserProviderListener {

    private var favoriteListener : FavoriteFragmentInterface? = null
    private var siteProvider = SiteProvider(this)
    private var userProvider = UserProvider(this)
    private lateinit var favoriteItemAdapter: FavoriteItemAdapter
    private lateinit var recyclerView : RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            favoriteListener = context as FavoriteFragmentInterface
        } catch (e: java.lang.ClassCastException){
            throw ClassCastException("$activity debe implementar el callback onFavoriteSelected")
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {
        userProvider.getFavorites()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root =  inflater.inflate(R.layout.fragment_list_layout, container, false)
        recyclerView = root.findViewById(R.id.recycler_view_item)
        recyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    interface FavoriteFragmentInterface{
        fun onFavoriteSelected(locationKey: String, secretNumber: Int)
        fun gotoFavorite(favorite: FavoritoUsuario)
    }

    override fun userDiscovered(list: List<SitioDescubierto>) {
    }

    override fun listReady() {
    }

    override fun typesFound(list: ArrayList<String>) {
    }

    override fun getSingleSite(site: Sitio, key: String) {
        Toast.makeText(context, site.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun changeFavorite(favorito: FavoritoUsuario, remove : Boolean) {
        if(remove){
            userProvider.removeFav(favorito.llave , favorito.numSecreto)
        }else{
            userProvider.addFavorite(favorito)
        }
    }

    override fun gotoLocation(favorito: FavoritoUsuario) {
        favoriteListener!!.gotoFavorite(favorito)
    }

    override fun onFavoriteChecked(isFav: List<Boolean>) {
    }

    override fun getUser(user: Usuario) {
    }

    override fun getAllFavorites(favoritos: List<FavoritoUsuario>) {
        favoriteItemAdapter = FavoriteItemAdapter(favoritos, this)
        recyclerView.adapter = favoriteItemAdapter
        (recyclerView.adapter as FavoriteItemAdapter).notifyDataSetChanged()
    }
}