package com.valentingonzalez.turistear.adapters


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.valentingonzalez.turistear.R
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.gallery_item_layout.view.*
import java.io.File
import java.lang.IllegalArgumentException

class ShareGridAdapter(private val images : MutableList<Uri>, private val imageSource: MutableList<String>, val  context: Context, val siteName: String) : Adapter<ShareGridAdapter.ViewHolder>(){

    private var multiSelect = false
    private var selectedItem : Uri? = null
    private var selectedCard : CardView? = null
    //private var items = arrayListOf<File>()
    private var progressDialog: AlertDialog? = null

    private var actionMode: ActionMode? = null
    inner class  mActionMode : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.menu_share -> {

                    progressDialog = SpotsDialog.Builder().setContext(context).setMessage("Descargando...").build()
                    progressDialog!!.show()

                    val outputDir = File(context.filesDir, "images")
                    if(!outputDir.exists()){
                        outputDir.mkdirs()
                    }

                    val temp = File(outputDir,"temp.png")
                    val fbStorage = FirebaseStorage.getInstance().getReferenceFromUrl(selectedItem.toString())
                    fbStorage.getFile(temp).addOnSuccessListener {
                            if(temp.canRead()){
                                progressDialog!!.dismiss()
                            }
                    }

                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_SUBJECT, "He visitado $siteName")
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, "Mira el lugar que he visitado")
                    intent.type = "text/plain"
                    val fileUri: Uri?= try{
                        FileProvider.getUriForFile(context,"com.valentingonzalez.turistear.fileprovider", temp)

                    }catch (e: IllegalArgumentException){
                        e.printStackTrace()
                        null
                    }
                    if(fileUri!=null) {
                        intent.putExtra(Intent.EXTRA_STREAM, fileUri)
                        intent.type = "image/*"
                        context.startActivity(Intent.createChooser(intent, "Choose an app to share"))
                    }



                }
                R.id.menu_delete->{
                    //delete selected items from firebase
                    val fbStorage = FirebaseStorage.getInstance().getReferenceFromUrl(selectedItem.toString())
                    Log.d("TESTDELETE",fbStorage.path)
                    fbStorage.delete().addOnSuccessListener {
                        Toast.makeText(context,"Deleted!",Toast.LENGTH_SHORT).show()
                    }
                    val selected = images.indexOf(selectedItem)
                    images.removeAt(selected)
                    imageSource.removeAt(selected)
                    notifyDataSetChanged()
                }
            }
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.image_contextual_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode:ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onDestroyActionMode(mode:ActionMode?) {
            multiSelect = false

            selectedItem = null
            actionMode = null
            if(selectedCard!=null)
                selectedCard!!.setBackgroundColor(Color.WHITE)

            //notifyDataSetChanged()
        }

    }

    class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val image : ImageView = view.card_grid_image
        val card : CardView = view.card_view
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item_layout, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = images[position]
        holder.image.setOnClickListener{
            if(actionMode == null) {
                actionMode = (it.context as AppCompatActivity).startSupportActionMode(mActionMode())
            }
            selectItem(currentItem, holder.card, position)
            val menu = actionMode?.menu!!
            menu.findItem(R.id.menu_delete).isVisible = imageSource[position] == "Personal"
        }
        Picasso.get()
                .load(currentItem)
                .into(holder.image)
    }

    private fun loadImage(name: String, image: ImageView) {
        Picasso.get().load(File(name)).into(image)
    }

    private fun selectItem(uri: Uri, cardView: CardView, position: Int) {
            if(selectedItem == uri){
                selectedItem = null
                cardView.setBackgroundColor(Color.WHITE)
                selectedCard = null
            }else{
                selectedItem = uri
                selectedCard?.setBackgroundColor(Color.WHITE)
                cardView.setBackgroundColor(Color.BLUE)
                selectedCard = cardView
            }

    }
    interface ManageImageSelection{
        fun onItemShare(list: ArrayList<Uri>)
    }
    fun addItem(item: Uri){
        this.images.add(item)
        notifyDataSetChanged()
    }
}