package com.valentingonzalez.turistear.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.StorageReference
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.adapters.GridAdapterv2
import com.valentingonzalez.turistear.includes.BasicToolbar.show
import com.valentingonzalez.turistear.providers.ImageSourcesProvider
import java.io.File

class ShareGalleryActivity: AppCompatActivity(), ImageSourcesProvider.ImageListener{

    private val imageProvider: ImageSourcesProvider = ImageSourcesProvider(this)
    private var imagesURLs = mutableListOf<Uri>()
    private var imagesSrc = mutableListOf<String>()
    private lateinit var gridView: RecyclerView

    private lateinit var privateRootDir: File
    // The path to the "images" subdirectory
    private lateinit var imagesDir: File
    // Array of files in the images subdirectory
    private lateinit var imageFiles: Array<File>
    // Array of filenames corresponding to imageFiles
    private lateinit var imageFilenames: Array<String>
    private lateinit var siteName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_image_gallery)
        // Get the files/ subdirectory of internal storage
        privateRootDir = filesDir
        // Get the files/images subdirectory;
        imagesDir = File(privateRootDir, "images")
        if(!imagesDir.exists()){
            imagesDir.mkdirs()
        }
        // Get the files in the images subdirectory
        imageFiles = imagesDir.listFiles()
        // Set the Activity's result to null to begin with
        setResult(Activity.RESULT_CANCELED, null)

        val bundle = intent.extras
        val uid = bundle!!.getString(getString(R.string.user_id))!!
        val siteId = bundle.getString(getString(R.string.marker_location_key))!!
        siteName = bundle.getString(getString(R.string.marker_title))!!
        gridView = findViewById(R.id.grid_view)

        imageProvider.getPersonalImages(true, uid, siteId)
        show(this@ShareGalleryActivity,"Imagenes de $siteName}",true)
    }

    override fun onImageObtained(images : List<StorageReference>, imagesSource: List<String>) {
        imagesSrc.addAll(imagesSource)
        val adapter = GridAdapterv2(imagesURLs, this, siteName)
        gridView.adapter = adapter
        for(it in images) {
            it.downloadUrl.addOnSuccessListener {
                result ->
                adapter.addItem(result)
                adapter.notifyDataSetChanged()
            }
        }
        gridView.layoutManager = GridLayoutManager(this, 2)
    }


}