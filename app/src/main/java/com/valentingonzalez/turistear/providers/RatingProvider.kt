package com.valentingonzalez.turistear.providers

import com.google.firebase.database.*
import com.valentingonzalez.turistear.models.Comentario

class RatingProvider (private val listener: SiteRatings){
    private var mRatingReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Comentarios")
    fun getRatings(key: String){
        mRatingReference.child(key).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
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
    interface SiteRatings{
        fun onRatingsRead(comentarios : List<Comentario>)
    }
}