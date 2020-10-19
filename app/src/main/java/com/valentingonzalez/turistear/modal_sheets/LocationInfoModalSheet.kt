package com.valentingonzalez.turistear.modal_sheets

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.valentingonzalez.turistear.activities.maps.CommentActivity
import com.valentingonzalez.turistear.activities.maps.SecretDetailActivity
import com.valentingonzalez.turistear.activities.maps.ShareGalleryActivity
import com.valentingonzalez.turistear.activities.camera.ARCameraActivity
import com.valentingonzalez.turistear.models.FavoritoUsuario
import com.valentingonzalez.turistear.models.Usuario
import com.valentingonzalez.turistear.providers.AuthProvider
import com.valentingonzalez.turistear.providers.SiteProvider
import com.valentingonzalez.turistear.providers.UserProvider
import com.valentingonzalez.turistear.providers.UserSecretProvider
import java.util.*
import kotlin.collections.HashMap

class LocationInfoModalSheet : BottomSheetDialogFragment(), UserSecretProvider.UserSecrets, UserProvider.UserProviderListener, TextToSpeech.OnInitListener{
    private var siteProvider: SiteProvider = SiteProvider(null)
    private var userSecretProvider : UserSecretProvider = UserSecretProvider(this)
    private var userProvider: UserProvider = UserProvider(this)
    private var mFirebaseAuth: AuthProvider = AuthProvider()
    private var mStorage : StorageReference = FirebaseStorage.getInstance().reference
    private lateinit var tts : TextToSpeech

    private lateinit var shareButton: ImageButton
    private lateinit var favoriteLocation: ImageButton
    private lateinit var ratingsButton : ImageButton
    private lateinit var ttsButton : ImageButton
    private lateinit var secretAmountText: TextView

    private lateinit var currLocation: String
    private lateinit var nombre: String
    private lateinit var imageSrc: String
    private var ratingValue: Double = 0.0



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.modal_sheet_v2, container, false)
        val b = arguments
        val titleView = layout.findViewById<TextView>(R.id.modal_location_title_tv)
        val descView = layout.findViewById<TextView>(R.id.modal_location_description)
        val showSecrets = layout.findViewById<ImageView>(R.id.show_secrets)
        val ratingView = layout.findViewById<TextView>(R.id.rating_location_value)
        secretAmountText = layout.findViewById(R.id.secrets_amount)

        tts = TextToSpeech(context, this)
        ttsButton = layout.findViewById(R.id.modal_tts_button)

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
            userSecretProvider.getSiteDiscoveredSecrets(FirebaseAuth.getInstance().currentUser!!.uid, currLocation)
        }
        showSecrets.setOnClickListener{
            val intent = Intent(context, SecretDetailActivity::class.java)
            intent.putExtra(getString(R.string.marker_location_key), currLocation)
            startActivity(intent)
        }

        nombre = b.getString(getString(R.string.marker_title))!!
        Log.d("SITERATINGMODAL", b.getDouble(getString(R.string.marker_location_rating)).toString())
        val rate = b.getDouble(getString(R.string.marker_location_rating))
        ratingView.setText("Rating $rate")
        //rating_location_value.text = "Rating ${b.getDouble(getString(R.string.marker_location_rating))}"
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

        ttsButton.setOnClickListener{
            tts.speak(descView.text.toString(), TextToSpeech.QUEUE_FLUSH, null, "")
        }

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

    override fun onFavoriteChecked(isFav: List<Boolean>) {
        if(isFav.isNotEmpty() && isFav[0]){
            changeFavIcon(isFav[0])
        }
    }

    override fun getUserName(user: Usuario) {
    }

    override fun getAllFavorites(favoritos: List<FavoritoUsuario>) {
    }

    override fun onSiteDiscoveredStatus(obtained: HashMap<Int, Boolean>) {
        var count = 0
        for(v in obtained){
            if(v.value){
                count++
            }
        }
        secretAmountText.text = "$count/3"
    }

    override fun onSecretDiscovered() {

    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts.setLanguage(Locale.getDefault())
            ttsButton.isEnabled = true
        }else{
            ttsButton.isEnabled = false
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}