package com.valentingonzalez.turistear.activities.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Preview
import androidx.camera.core.impl.PreviewConfig

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.valentingonzalez.turistear.R
import kotlinx.android.synthetic.main.camera_qr_layout.*

class QRCameraActivity : AppCompatActivity(){

    private lateinit var currLocation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_qr_layout)
        currLocation = intent.getStringExtra(getString(R.string.marker_location_key))!!
        if(allPermisionsGranted()){
            camera_texture_view.post{ startCamera()}
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun allPermisionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermisionsGranted()){
                camera_texture_view.post{ startCamera()}
            }
        } else {
            Toast.makeText(this,
                    "No se tiene permiso para usar la camara",
                    Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun startCamera() {
        val previewConfig = Preview.Builder().build()


    }

    companion object {
        private const val TAG = "CameraXAR"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 11
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}