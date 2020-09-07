package com.valentingonzalez.turistear.providers

import android.net.Uri
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URL

class ImageSourcesProvider (private val mListener: ImageListener){

    private val imageReferences  = FirebaseStorage.getInstance().reference
    private var imagesURLs : MutableList<StorageReference> = mutableListOf()
    private var imageSrc : MutableList<String> = mutableListOf()

    fun getPersonalImages(all: Boolean, uid: String, siteId: String){
        imageReferences.child(uid+"/"+siteId).listAll().addOnSuccessListener {
            for( item in it.items){
               imagesURLs.add(item)
                imageSrc.add("Personal")
            }
            if(all){
                getSiteImages(all, siteId)
            }else{
                mListener.onImageObtained(imagesURLs, imageSrc)
            }
        }
    }

    fun getSiteImages(all: Boolean, siteId: String){
        imageReferences.child(siteId).listAll().addOnSuccessListener {
            for( item in it.items){
                imagesURLs.add(item)
                imageSrc.add("Site")
            }
            if(all){
                getSecretImages(siteId, listOf(0,1,2))
            }else{
                mListener.onImageObtained(imagesURLs, imageSrc)
            }
        }
    }

    fun getSecretImages(siteId: String, secrets: List<Int>){
        imageReferences.child(siteId+"/secrets").listAll().addOnSuccessListener {
            for (item in it.items){
                imagesURLs.add(item)
                imageSrc.add("Secret")
            }
            mListener.onImageObtained(imagesURLs, imageSrc)
        }
    }

    interface ImageListener{
        fun onImageObtained(imagesReferences: List<StorageReference>, sources : List<String>)
    }
}