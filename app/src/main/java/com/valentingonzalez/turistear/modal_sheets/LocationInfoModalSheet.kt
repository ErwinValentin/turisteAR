package com.valentingonzalez.turistear.modal_sheets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.activities.CommentActivity
import com.valentingonzalez.turistear.activities.SecretDetailActivity
import com.valentingonzalez.turistear.activities.ShareGalleryActivity
import com.valentingonzalez.turistear.activities.camera.ARCameraActivity
import com.valentingonzalez.turistear.models.FavoritoUsuario
import com.valentingonzalez.turistear.providers.AuthProvider
import com.valentingonzalez.turistear.providers.SiteProvider
import com.valentingonzalez.turistear.providers.UserProvider
import kotlinx.android.synthetic.main.modal_sheet_v2.*
import java.util.*

class LocationInfoModalSheet : BottomSheetDialogFragment(), SiteProvider.DiscoveredSites , UserProvider.UserProviderListener{
    private var siteProvider: SiteProvider = SiteProvider(this)
    private var userProvider: UserProvider = UserProvider(this)
    private var mFirebaseAuth: AuthProvider = AuthProvider()
    private var mStorage : StorageReference = FirebaseStorage.getInstance().reference


    private lateinit var shareButton: ImageButton
    private lateinit var favoriteLocation: ImageButton
    private lateinit var ratingsButton : ImageButton

    private lateinit var currLocation: String
    private lateinit var nombre: String
    private lateinit var imageSrc: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //TODO get secret amount from user, show on layout
        val layout = inflater.inflate(R.layout.modal_sheet_v2, container, false)
        val b = arguments
        val titleView = layout.findViewById<TextView>(R.id.modal_location_title_tv)
        val descView = layout.findViewById<TextView>(R.id.modal_location_description)
        val showSecrets = layout.findViewById<ImageView>(R.id.show_secrets)
        //val secretsAmount = layout.findViewById<TextView>(R.id.secrets_amount)
        //locationID = b?.getString("location").toString()
        val main_image = layout.findViewById<ImageView>(R.id.modal_location_main_image)
        currLocation = b?.getString(getString(R.string.marker_location_key))!!
        imageSrc = b.getString(getString(R.string.marker_image))!!
        val dlPath = mStorage.child(currLocation+"/"+imageSrc)
        Log.d("IMAGE", dlPath.toString())
        dlPath.downloadUrl.addOnSuccessListener {
            Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.landscape_sample)
                    .into(main_image)
        }
        if(mFirebaseAuth.currentUser()!=null){
            siteProvider.getSiteDiscoveredSecrets(FirebaseAuth.getInstance().currentUser!!.uid, currLocation)
        }
        showSecrets.setOnClickListener{
            val intent = Intent(context, SecretDetailActivity::class.java)
            intent.putExtra(getString(R.string.marker_location_key), currLocation)
            startActivity(intent)
        }

        nombre = b.getString(getString(R.string.marker_title))!!
        titleView.text = nombre
        descView.text = b.getString(getString(R.string.marker_description))
        val openCamera: ImageButton = layout.findViewById(R.id.modal_camera_button)
        openCamera.setOnClickListener{
            val intent = Intent(context, ARCameraActivity::class.java)
            intent.putExtra(getString(R.string.marker_location_key), currLocation)
            startActivity(intent)
        }
        favoriteLocation = layout.findViewById(R.id.modal_favorite_button)
        favoriteLocation.tag = 0
        userProvider.isFavorite(currLocation, listOf(-1))
        favoriteLocation.setOnClickListener{
            if(favoriteLocation.tag == 0){
                changeFavIcon(true)
                userProvider.addFavorite(FavoritoUsuario(currLocation,nombre,-1, Calendar.getInstance().time.toString()))
            }else{
                changeFavIcon(false)
                userProvider.removeFav(currLocation,-1)

            }
        }

        ratingsButton = layout.findViewById(R.id.modal_reviews_button)
        ratingsButton.setOnClickListener{
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra(getString(R.string.marker_location_key), currLocation)
            intent.putExtra(getString(R.string.marker_title), nombre)
            startActivity(intent)
        }
        shareButton = layout.findViewById(R.id.modal_share_button)
        shareButton.setOnClickListener{
            val intent = Intent(context, ShareGalleryActivity::class.java)
            intent.putExtra(getString(R.string.user_id), FirebaseAuth.getInstance().currentUser!!.uid)
            intent.putExtra(getString(R.string.marker_location_key), currLocation)
            intent.putExtra(getString(R.string.marker_title), nombre)
            startActivity(intent)
        }
        /*val userProvider = UserProvider()
        userProvider.getUser(nv)*/

     /*   val cameraButton : ImageButton = layout.findViewById(R.id.open_camera_here_button)
        cameraButton.setOnClickListener{
            startActivity( Intent(activity,CameraActivity1::class.java))
        }

        val icon1: ImageView = layout.findViewById<ImageView>(R.id.chest_icon1)
        icon1.setOnClickListener {
            Toast.makeText(activity,"Clicked Icon 1", Toast.LENGTH_SHORT).show()
        }
        val icon2: ImageView = layout.findViewById<ImageView>(R.id.chest_icon2)
        icon2.setOnClickListener {
            Toast.makeText(activity,"Clicked Icon 2", Toast.LENGTH_SHORT).show()
        }
        val icon3: ImageView = layout.findViewById<ImageView>(R.id.chest_icon3)
        icon3.setOnClickListener {
            Toast.makeText(activity,"Clicked Icon 3", Toast.LENGTH_SHORT).show()
        }

        val favoriteIcon: ImageView = layout.findViewById((R.id.favorite_location))
        favoriteIcon.tag = 1
        favoriteIcon.setOnClickListener{
            if(it.tag==1){
                favoriteIcon.setImageResource(R.drawable.ic_star_green_a700_24dp)
                it.tag = 2
            }else{
                favoriteIcon.setImageResource(R.drawable.ic_star_border_light_green_600_24dp)
                it.tag = 1
            }
        }*/
        return layout
    }

    private fun changeFavIcon(isFav : Boolean) {
        if(favoriteLocation.tag == 0 && isFav) {
            favoriteLocation.setImageResource(R.drawable.ic_favorite_red_400_24dp)
            favoriteLocation.tag = 1
        }
        if(favoriteLocation.tag == 1 && !isFav) {
            favoriteLocation.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            favoriteLocation.tag = 0
        }
    }

    override fun onDiscovered(lista: List<Boolean>) {
        val count = lista.count{ it }
        secrets_amount.text = "$count/3"
    }

    override fun onFavoriteChecked(isFav: List<Boolean>) {
        if(isFav.isNotEmpty() && isFav[0]){
            changeFavIcon(isFav[0])
        }
    }

    override fun getUserName(name: String) {
    }
}