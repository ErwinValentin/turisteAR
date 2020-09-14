package com.valentingonzalez.turistear.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.adapters.SecretDetailAdapter
import com.valentingonzalez.turistear.models.Secreto
import com.valentingonzalez.turistear.providers.AuthProvider
import com.valentingonzalez.turistear.providers.SecretProvider
import com.valentingonzalez.turistear.providers.SiteProvider
import com.valentingonzalez.turistear.providers.UserProvider
import com.valentingonzalez.turistear.providers.UserSecretProvider
import com.valentingonzalez.turistear.includes.BasicToolbar.show
import com.valentingonzalez.turistear.models.Usuario


class SecretDetailActivity : AppCompatActivity(), SecretProvider.SiteSecrets, UserProvider.UserProviderListener, UserSecretProvider.UserSecrets{

    private val secretProvider: SecretProvider = SecretProvider(this)
    private var siteProvider: SiteProvider = SiteProvider(null)
    private val userSecretProvider: UserSecretProvider = UserSecretProvider(this)
    private val userProvider: UserProvider = UserProvider(this)
    private var mFirebaseAuth: AuthProvider = AuthProvider()
    private lateinit var currLocation: String
    private lateinit var obtainedList: List<Boolean>
    private lateinit var siteSecretsList: List<Secreto>
    private lateinit var secretsRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_list_layout)
        secretsRecyclerView = findViewById(R.id.recycler_view_item)
        show(this, "Secretos del Lugar", true)
        Log.d("Extras", intent.extras?.getString(getString(R.string.marker_location_key))!!)
        currLocation = intent.extras?.getString(getString(R.string.marker_location_key))!!
        if(mFirebaseAuth.currentUser()!=null){
            userSecretProvider.getSiteDiscoveredSecrets(FirebaseAuth.getInstance().uid.toString(),currLocation)
        }
    }


    override fun onSecretDiscovered(secretList: List<Secreto>) {
        siteSecretsList = secretList
        userProvider.isFavorite(currLocation, listOf(0,1,2))
    }

    override fun onFavoriteChecked(isFav: List<Boolean>) {
        Log.d("ACTIVITYFAVS",isFav.toString())
        val adapter = SecretDetailAdapter(siteSecretsList, obtainedList, isFav, currLocation, userProvider)
        secretsRecyclerView.adapter = adapter
        secretsRecyclerView.layoutManager = LinearLayoutManager(this)
        secretsRecyclerView.setHasFixedSize(true)
    }

    override fun getUserName(user: Usuario) {
    }

    override fun onSiteDiscoveredStatus(obtained: List<Boolean>) {
        obtainedList = obtained
        secretProvider.getSecrets(currLocation)
    }

    override fun onSecretDiscovered() {

    }

}
