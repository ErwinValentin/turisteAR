package com.valentingonzalez.turistear.activities.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.valentingonzalez.turistear.R
import kotlinx.android.synthetic.main.camera_layout1.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity1 : AppCompatActivity() {
    private var imageCapture : ImageCapture? = null

    private lateinit var outputDir: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var currLocation: String

    override fun onCreate( savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_layout1)
        currLocation = intent.getStringExtra(getString(R.string.marker_location_key))!!
        if(allPermisionsGranted()){
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        camera_capture_button.setOnClickListener{ takePhoto() }
        outputDir = File(applicationContext.getExternalFilesDir(null).toString()+"/TouristeAR/"+currLocation)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
       val imageCapture = imageCapture?:return
        if(!outputDir.exists()){
            outputDir.mkdirs()
        }
        Log.d("DIR", outputDir.exists().toString())
        val photoFile = File(
                outputDir,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis())+".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback{
                override fun onError( exc: ImageCaptureException){
                    Log.e(TAG, "Error al tomar foto: ${exc.message}", exc)
                }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val saverUri = Uri.fromFile(photoFile)
                val msg = "Foto tomada exitosamente, guardando en linea..."
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                Log.d(TAG, msg)

                uploadToFirebase(photoFile)
            }
        })
    }

    private fun uploadToFirebase(photoFile: File) {
        val storageRef = Firebase.storage.reference

        val file = Uri.fromFile(photoFile)
        val metadata = storageMetadata {
            contentType = "image/jpg"
        }
        val photoRef = storageRef.child(FirebaseAuth.getInstance().uid.toString())
                .child(currLocation).child(file.lastPathSegment.toString())

        val uploadTask = photoRef.putFile(file, metadata)

        uploadTask.addOnFailureListener{
            Toast.makeText(this, "Error uploading", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                    .build()
                    .also{
                        it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                    }
            imageCapture = ImageCapture.Builder()
                    .build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture)
            }catch (exc: Exception){
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermisionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
         requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ){
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermisionsGranted()){
                startCamera()
            }
        } else {
            Toast.makeText(this,
                    "No se tiene permiso para usar la camara",
                    Toast.LENGTH_SHORT).show()
            finish()
        }

    }
    private fun getOutputDirectory() : File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}