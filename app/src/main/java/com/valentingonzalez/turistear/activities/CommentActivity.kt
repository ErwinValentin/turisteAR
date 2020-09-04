package com.valentingonzalez.turistear.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.adapters.CommentAdapter
import com.valentingonzalez.turistear.includes.BasicToolbar
import com.valentingonzalez.turistear.models.Comentario
import com.valentingonzalez.turistear.providers.RatingProvider

class CommentActivity : AppCompatActivity(), RatingProvider.SiteRatings{

    private lateinit var ratingRecyclerView: RecyclerView
    private val ratingsProvider = RatingProvider(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_list_layout)
        ratingRecyclerView = findViewById(R.id.recycler_view_item)
        BasicToolbar.show(this, "Reseñas del Lugar", true)
        val key: String = intent.getStringExtra(getString(R.string.marker_location_key))!!
        ratingsProvider.getRatings(key)
    }

    override fun onRatingsRead(comentarios: List<Comentario>) {
       val adapter = CommentAdapter(comentarios)
        ratingRecyclerView.adapter = adapter
        ratingRecyclerView.layoutManager = LinearLayoutManager(this)
        ratingRecyclerView.setHasFixedSize(true)
    }

}