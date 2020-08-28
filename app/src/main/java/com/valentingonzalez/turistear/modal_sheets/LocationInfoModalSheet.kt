package com.valentingonzalez.turistear.modal_sheets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.activities.camera.CameraActivity1
import com.valentingonzalez.turistear.providers.UserProvider

class LocationInfoModalSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.info_modal_sheet, container, false)
        val b = arguments
        val nv = layout.findViewById<TextView>(R.id.modal_location_title_tv)
        /*val userProvider = UserProvider()
        userProvider.getUser(nv)*/

        val cameraButton : ImageButton = layout.findViewById(R.id.open_camera_here_button)
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
        }
        return layout
    }
}