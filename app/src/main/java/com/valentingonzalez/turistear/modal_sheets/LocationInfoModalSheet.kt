package com.valentingonzalez.turistear.modal_sheets

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.activities.camera.CameraActivity1
import com.valentingonzalez.turistear.providers.UserProvider
import kotlinx.android.synthetic.main.modal_sheet_v2.*

class LocationInfoModalSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.modal_sheet_v2, container, false)
        val b = arguments
        val nv = layout.findViewById<TextView>(R.id.modal_location_title_tv)
        val show_hide = layout.findViewById<ImageView>(R.id.show_secrets)

        val main_image = layout.findViewById<ImageView>(R.id.modal_location_main_image)
        show_hide.setOnClickListener{
            //TODO start detail activity
        }

        /*val userProvider = UserProvider()
        userProvider.getUser(nv)*/

     /*   val cameraButton : ImageButton = layout.findViewById(R.id.open_camera_here_button)
        cameraButton.setOnClickListener{
            startActivity( Intent(activity,CameraActivity1::class.java))
        }

        val icon1: ImageView = layout.findViewById<ImageView>(R.id.chest_icon1)
        icon1.setOnClickListener {
            Toast.makeText(activity,"Clicked Icon 1", Toast.LENGTH_SHORT).show()
        }
        val icon2: ImageView = layout.findViewById<ImageView>(R.id.chest_icon2)
        icon2.setOnClickListener {
            Toast.makeText(activity,"Clicked Icon 2", Toast.LENGTH_SHORT).show()
        }
        val icon3: ImageView = layout.findViewById<ImageView>(R.id.chest_icon3)
        icon3.setOnClickListener {
            Toast.makeText(activity,"Clicked Icon 3", Toast.LENGTH_SHORT).show()
        }

        val favoriteIcon: ImageView = layout.findViewById((R.id.favorite_location))
        favoriteIcon.tag = 1
        favoriteIcon.setOnClickListener{
            if(it.tag==1){
                favoriteIcon.setImageResource(R.drawable.ic_star_green_a700_24dp)
                it.tag = 2
            }else{
                favoriteIcon.setImageResource(R.drawable.ic_star_border_light_green_600_24dp)
                it.tag = 1
            }
        }*/
        return layout
    }
}