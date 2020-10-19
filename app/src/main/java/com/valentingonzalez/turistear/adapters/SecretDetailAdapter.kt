package com.valentingonzalez.turistear.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.FavoritoUsuario
import com.valentingonzalez.turistear.models.Secreto
import com.valentingonzalez.turistear.providers.UserProvider
import kotlinx.android.synthetic.main.secret_detail_item.view.*
import java.util.*
import kotlin.collections.HashMap


class SecretDetailAdapter(
        private val secrets: List<Secreto>,
        private val obtained: HashMap<Int, Boolean>,
        private val favorites: List<Boolean>,
        private val currLocation: String,
        private val userProvider: UserProvider,
        val ttsListener: SecretListener
    )
    : Adapter<SecretDetailAdapter.ViewHolder>(){

    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val secretTitle : TextView = view.secret1_title
        val secretDescription : TextView = view.secret1_description
        val secretObtainedIcon : ImageView = view.secret1_obtained_status
        val secretFavorite : ImageView = view.secret1_favorite_button
        val secretTTS : ImageView = view.secret1_tts_button
        val secretMainImage : ImageView = view.secret1_main_image
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecretDetailAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.secret_detail_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return secrets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = secrets[position]
        val currentObtained = obtained[position]
        val isFav = favorites[position]

        val fbStorage = FirebaseStorage.getInstance().reference
        fbStorage.child("$currLocation/secrets/${currentItem.recursos!![0].valor}").downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).placeholder(R.drawable.landscape_sample).into(holder.secretMainImage)
        }
        holder.secretTitle.isSelected = true
        if(currentObtained==true) {
            holder.secretObtainedIcon.setImageResource(R.drawable.open_chest_v2_m)
            holder.secretTitle.text = currentItem.nombre
            holder.secretDescription.text = currentItem.descripcion
            holder.secretTTS.visibility = View.VISIBLE
            holder.secretTTS.setOnClickListener{
                ttsListener.ttsDescription(currentItem.descripcion.toString())
            }
        }else{
            holder.secretObtainedIcon.setImageResource(R.drawable.closed_chest_m)
            holder.secretMainImage.visibility = View.GONE
            holder.secretDescription.visibility = View.GONE
            holder.secretFavorite.visibility = View.INVISIBLE
            holder.secretTitle.text = "Secreto aún no descubierto"
            holder.secretTTS.visibility = View.GONE
        }
        if(isFav) {
            holder.secretFavorite.setImageResource(R.drawable.ic_favorite_red_400_24dp)
            holder.secretFavorite.tag = 1
        }else{
            holder.secretFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            holder.secretFavorite.tag = 0
        }
        holder.secretFavorite.setOnClickListener{
            if(it.tag==0){
                holder.secretFavorite.setImageResource(R.drawable.ic_favorite_red_400_24dp)
                it.tag = 1
                val fav = FavoritoUsuario(currLocation, currentItem.nombre,position ,Calendar.getInstance().time.toString())
                userProvider.addFavorite(fav)

            }else{
                holder.secretFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                it.tag = 0
                userProvider.removeFav(currLocation, position)
            }
        }
    }
    interface SecretListener{
        fun ttsDescription(description: String)
    }
}