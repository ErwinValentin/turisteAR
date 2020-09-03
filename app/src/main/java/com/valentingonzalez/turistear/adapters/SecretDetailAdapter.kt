package com.valentingonzalez.turistear.adapters


import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.valentingonzalez.turistear.models.Secreto
import kotlinx.android.synthetic.main.secret_detail_item.view.*

class SecretDetailAdapter(private val secrets : List<Secreto>, private val obtained: List<Boolean>) : Adapter<SecretDetailAdapter.ViewHolder>(){
    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val secretTitle : TextView = view.secret1_title
        val secretDescription : TextView = view.secret1_description
        val secretObtainedIcon : ImageView = view.secret1_obtained_status
        val secretFavorite : ImageView = view.secret1_favorite_button
        val secretMainImage : ImageView = view.secret1_main_image
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecretDetailAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: SecretDetailAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}