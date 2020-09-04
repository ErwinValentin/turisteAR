package com.valentingonzalez.turistear.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.Comentario
import kotlinx.android.synthetic.main.rating_comment_item.view.*

class CommentAdapter(private val comments: List<Comentario>): RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val userIcon: ImageView = view.review_item_user_icon
        val title: TextView = view.review_item_title
        val date: TextView = view.review_item_review_date
        val score: TextView = view.review_item_score
        val content: TextView = view.review_item_content
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rating_comment_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentComment = comments[position]

        holder.title.text = currentComment.titulo
        holder.content.text = currentComment.comentario
        holder.date.text = currentComment.fecha.toString()
        holder.score.text = "${currentComment.calificacion}/5"

    }

}