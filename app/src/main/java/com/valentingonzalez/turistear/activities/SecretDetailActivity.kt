package com.valentingonzalez.turistear.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.Secreto
import com.valentingonzalez.turistear.providers.AuthProvider
import com.valentingonzalez.turistear.providers.SecretProvider
import com.valentingonzalez.turistear.providers.SiteProvider

class SecretDetailActivity : AppCompatActivity(), SiteProvider.DiscoveredSites, SecretProvider.SiteSecrets{

    val secretProvider: SecretProvider = SecretProvider(this)
    private var siteProvider: SiteProvider = SiteProvider(this)
    private var mFirebaseAuth: AuthProvider = AuthProvider()
    private lateinit var currLocation: String
    private lateinit var obtainedList: List<Boolean>
    private lateinit var siteSecretsList: List<Secreto>
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.secret_location_layout)
        Log.d("Extras", intent.extras?.getString(getString(R.string.marker_location_key))!!)
        currLocation = intent.extras?.getString(getString(R.string.marker_location_key))!!
        if(mFirebaseAuth.currentUser()!=null){
            siteProvider.getSiteDiscoveredSecrets(FirebaseAuth.getInstance().uid.toString(),currLocation)
        }
    }


    override fun onDiscovered(lista: List<Boolean>) {
        obtainedList = lista
        secretProvider.getSecrets(currLocation)
    }

    override fun onSecretDiscovered(secretList: List<Secreto>) {
        siteSecretsList = secretList
        Log.d("SECRET DETAIL",siteSecretsList.toString())
        Log.d("SECRET DETAIL",obtainedList.toString())
    }

}
