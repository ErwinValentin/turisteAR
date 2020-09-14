package com.valentingonzalez.turistear.providers

import android.util.Log
import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.Comentario
import kotlin.math.round

class RatingProvider (private val listener: SiteRatings){
    private var mRatingReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Comentarios")
    fun getRatings(key: String){
        mRatingReference.child(key).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val ratingList : MutableList<Comentario> = mutableListOf()
                if(snapshot.exists()){
                    for(rating in snapshot.children){
                        val r = rating.getValue(Comentario::class.java)
                        ratingList.add(r!!)
                    }
                    listener.onRatingsRead(ratingList)
                }
            }
        })
    }
    fun addRating(locationKey: String, rating: Comentario){
        mRatingReference.child(locationKey).push().setValue(rating)
        mRatingReference.child(locationKey).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var rating : Double = 0.0
                var count = 0
                for( rate in snapshot.children){
                    val r = rate.getValue(Comentario:: class.java)
                    rating+= r!!.calificacion!!
                    count++
                }
                Log.d("RATING AVERAGE", (rating/count).toString())
                val avg = ((rating/count)*100).toInt()
                val avg2Dec  = avg/100.0
                FirebaseDatabase.getInstance().reference.child("Sitios").child(locationKey).child("rating").setValue(avg2Dec)
            }

        })
    }
    interface SiteRatings{
        fun onRatingsRead(comentarios : List<Comentario>)
    }
}