package com.valentingonzalez.turistear.activities.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.squareup.picasso.Picasso
import com.valentingonzalez.turistear.utils.BarcodeAnalyzer
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.Secreto
import com.valentingonzalez.turistear.providers.SecretProvider
import com.valentingonzalez.turistear.providers.UserSecretProvider
import kotlinx.android.synthetic.main.camera_ar_layout.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.HashMap

class ARCameraActivity : AppCompatActivity(), UserSecretProvider.UserSecrets, SecretProvider.SiteSecrets{
    private var imageCapture: ImageCapture? = null
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var outputDir: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var markerLocation: String
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val userSecretProvider = UserSecretProvider(this)
    private val secretProvider = SecretProvider(this)
//    private val latlngList = mutableListOf<LocationData>()
    private var listaLlaves = mutableListOf<String>()
    private lateinit var listaDescubiertos: HashMap<Int, Boolean>
    private var listaSecretos = mutableListOf<Secreto>()
    private val uId = FirebaseAuth.getInstance().uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_ar_layout)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val lastLocation = fusedLocationProviderClient.lastLocation
        lastLocation.addOnSuccessListener { location ->
            if(location != null){
                currentLocation =location
            }
        }

        markerLocation = intent.getStringExtra(getString(R.string.marker_location_key))!!
        for (i in 0..2) {
            listaLlaves.add(markerLocation + i)
        }

        secretProvider.getSecrets(markerLocation)
        userSecretProvider.getSiteDiscoveredSecrets(uId, markerLocation)
//        secretProvider.getSecretKeys(currLocation)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
//        arLocalizerView = findViewById(R.id.arLocalizer)
//        arLocalizerView.onCreate(this)
//        show_locations_button.tag = 0
//        show_locations_button.setOnClickListener{
//            if(it.tag==0) {
//                viewFinder.visibility = View.INVISIBLE
//                arLocalizerView.visibility = View.VISIBLE
//                arLocalizerView.setDestinations(latlngList as List<LocationData>)
//                it.tag = 1
//            }else{
//                viewFinder.visibility = View.INVISIBLE
//                arLocalizerView.visibility = View.VISIBLE
//                it.tag = 0
//            }
//        }
        camera_capture_button.setOnClickListener { takePhoto() }
        var scan = false
        var scannedCode = ""
        qr_scan_button.setOnClickListener { button ->
//            val intent = Intent(this, QRCameraActivity::class.java)
//            intent.putExtra(getString(R.string.marker_location_key), currLocation)
//            startActivity(intent)
            if (!scan) {
                scan = true
                qr_scan_button.setImageResource(R.drawable.qr_white_cancel)
                imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), BarcodeAnalyzer {
                    if (scannedCode != it) {
                        scannedCode = it
                        Log.d("CODIGO", it)
                        Log.d("LISTA CONTIENE", listaLlaves.contains(it).toString())
                        if (listaLlaves.contains(it)) {
                            var numero = it.substring(it.length - 1).toInt()
                            if (listaDescubiertos[numero] != null) {
                                Toast.makeText(this, "Ya has descubierto este secreto", Toast.LENGTH_SHORT).show()
                            } else {
                                val secreto = listaSecretos[numero]
                                val loc = Location("")
                                loc.latitude = secreto.latitud!!
                                loc.longitude = secreto.longitud!!
                                if (loc.distanceTo(currentLocation) < 50) {
                                    userSecretProvider.addSecretToDiscovered(uId, markerLocation, numero, listaSecretos[numero].nombre.toString())
                                } else {
                                    Toast.makeText(this, "Este secreto esta muy lejos", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            Toast.makeText(this, "Error con el codigo", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                scan = false
                qr_scan_button.setImageResource(R.drawable.qr_white)
                imageAnalyzer.clearAnalyzer()
            }
        }


        outputDir = File(applicationContext.getExternalFilesDir(null).toString() + "/TouristeAR/" + markerLocation)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        //Log.d("DIR", outputDir.exists().toString())
        val photoFile = File(
                outputDir,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Error al tomar foto: ${exc.message}", exc)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                //val saverUri = Uri.fromFile(photoFile)
                val msg = "Foto tomada exitosamente, guardando en linea..."
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                Log.d(TAG, msg)
                //TODO mover a servicio aparte para guardar en el fondo, aun si cierran la aplicacion
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
                .child(markerLocation).child(file.lastPathSegment.toString())

        val uploadTask = photoRef.putFile(file, metadata)

        uploadTask.addOnFailureListener {
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
                    .also {
                        it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                    }
            imageCapture = ImageCapture.Builder()
                    .build()

            imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setTargetRotation(viewFinder.display.rotation)
                    .build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalyzer)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            }
        } else {
            Toast.makeText(this,
                    "No se tiene permiso para usar la camara",
                    Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun getOutputDirectory(): File {
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
        private const val TAG = "CameraXAR"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onSiteDiscoveredStatus(obtained: HashMap<Int, Boolean>) {
        listaDescubiertos = HashMap(obtained)
    }

    override fun onSecretDiscovered() {
        Snackbar.make(qr_scan_button , "¡Has descubierto un secreto!", Snackbar.LENGTH_SHORT).show()
    }

    override fun onSecretDiscovered(secretList: List<Secreto>) {
        listaSecretos.addAll(secretList)
//        for(secret in secretList){
//            latlngList.add(LocationData(secret.latitud!!,secret.longitud!!))
//        }
    }
}