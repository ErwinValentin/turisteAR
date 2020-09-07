package com.valentingonzalez.turistear.adapters

import android.content.Context
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.valentingonzalez.turistear.R

class ImageGalleryGridAdapter (context: Context,val resource: Int): ArrayAdapter<ImageGalleryGridAdapter.ItemHolder>(context, resource ){
    private var  itemList: MutableList<Uri> = mutableListOf()
    override fun getCount(): Int {
        return this.itemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ItemHolder
        if(convertView == null){
           convertView = LayoutInflater.from(context).inflate(resource,null)
            holder = ItemHolder()
            holder.image =convertView!!.findViewById<ImageView>(R.id.card_grid_image)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemHolder
        }
        Log.d("TIEM", itemList[position].toString())
        Picasso.get()
                .load(itemList[position])
                .placeholder(R.drawable.landscape_sample)
                .into(holder.image)
        return convertView
    }
    fun addItem(item: Uri){
        this.itemList.add(item)
        notifyDataSetChanged()
    }
    class ItemHolder {
        var image : ImageView ? = null
    }

}