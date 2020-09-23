package com.valentingonzalez.turistear.activities.maps

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.adapters.CommentAdapter
import com.valentingonzalez.turistear.fragments.AddReviewDialogFragment
import com.valentingonzalez.turistear.includes.BasicToolbar
import com.valentingonzalez.turistear.models.Comentario
import com.valentingonzalez.turistear.models.FavoritoUsuario
import com.valentingonzalez.turistear.models.Usuario
import com.valentingonzalez.turistear.providers.RatingProvider
import com.valentingonzalez.turistear.providers.UserProvider
import java.util.*

class CommentActivity : AppCompatActivity(), RatingProvider.SiteRatings, AddReviewDialogFragment.AddReviewListener, UserProvider.UserProviderListener{

    private lateinit var ratingRecyclerView: RecyclerView
    private val ratingsProvider = RatingProvider(this)
    var mUserProvider: UserProvider = UserProvider(this)
    private lateinit var locationKey : String
    private lateinit var locationName : String
    private lateinit var newRating : Comentario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_list_layout)
        ratingRecyclerView = findViewById(R.id.recycler_view_item)
        locationKey = intent.getStringExtra(getString(R.string.marker_location_key))!!
        locationName = intent.getStringExtra(getString(R.string.marker_title))!!
        BasicToolbar.show(this, locationName, true)
        ratingsProvider.getRatings(locationKey)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.reviews_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when ( item.itemId){
            R.id.add_review ->{
                showAddDialog()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddDialog(): Boolean {
        val fragmentManager = supportFragmentManager
        val dialogReview = AddReviewDialogFragment()
        dialogReview.show(fragmentManager, "Agregar Reseña")
        return true
    }

    override fun onRatingsRead(comentarios: List<Comentario>) {
       val adapter = CommentAdapter(comentarios)
        ratingRecyclerView.adapter = adapter
        ratingRecyclerView.layoutManager = LinearLayoutManager(this)
        ratingRecyclerView.setHasFixedSize(true)
    }

    override fun onDialogPositiveClick(title: String, comment: String, stars: Float) {
        newRating = Comentario(comment, stars, Calendar.getInstance().time.toString(), FirebaseAuth.getInstance().currentUser!!.uid, "", title)
        mUserProvider.getUser()
    }

    override fun onFavoriteChecked(isFav: List<Boolean>) {
    }

    override fun getUserName(user: Usuario) {
        newRating.nombreUsuario = user.nombre
        ratingsProvider.addRating(locationKey , newRating)
    }

    override fun getAllFavorites(favoritos: List<FavoritoUsuario>) {
    }
}