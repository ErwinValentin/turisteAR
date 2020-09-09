package com.valentingonzalez.turistear.providers

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ImageSourcesProvider (private val mListener: ImageListener){

    private val imageReferences  = FirebaseStorage.getInstance().reference
    private var imagesURLs : MutableList<StorageReference> = mutableListOf()

    fun getPersonalImages(all: Boolean, uid: String, siteId: String){
        imageReferences.child(uid+"/"+siteId).listAll().addOnSuccessListener {
            for( item in it.items){
               imagesURLs.add(item)
            }
            if(all){
                getSiteImages(all, siteId)
            }else{
                mListener.onImageObtained(imagesURLs)
            }
        }
    }

    fun getSiteImages(all: Boolean, siteId: String){
        imageReferences.child(siteId).listAll().addOnSuccessListener {
            for( item in it.items){
                imagesURLs.add(item)
            }
            if(all){
                getSecretImages(siteId, listOf(0,1,2))
            }else{
                mListener.onImageObtained(imagesURLs)
            }
        }
    }

    fun getSecretImages(siteId: String, secrets: List<Int>){
        imageReferences.child(siteId+"/secrets").listAll().addOnSuccessListener {
            for (item in it.items){
                imagesURLs.add(item)
            }
            mListener.onImageObtained(imagesURLs)
        }
    }

    interface ImageListener{
        fun onImageObtained(imagesReferences: List<StorageReference>)
    }
}