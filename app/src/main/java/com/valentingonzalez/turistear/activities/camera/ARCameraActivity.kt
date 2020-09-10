package com.valentingonzalez.turistear.activities.camera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.valentingonzalez.turistear.BarcodeAnalyzer
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

class ARCameraActivity : AppCompatActivity(), UserSecretProvider.UserSecrets {
    private var imageCapture: ImageCapture? = null
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var outputDir: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var currLocation: String
    private val userSecretProvider = UserSecretProvider(this)
    private var listaLlaves = mutableListOf<String>()
    private var listaDescubiertos = mutableListOf<Boolean>()
    private val uId = FirebaseAuth.getInstance().uid.toString()
//    private val secretProvider: SecretProvider = SecretProvider(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_ar_layout)

        currLocation = intent.getStringExtra(getString(R.string.marker_location_key))!!
        for (i in 0..2) {
            listaLlaves.add(currLocation + i)
        }

        userSecretProvider.getSiteDiscoveredSecrets(uId, currLocation)
//        secretProvider.getSecretKeys(currLocation)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        camera_capture_button.setOnClickListener { takePhoto() }
        var scan = false
        var scannedCode = ""
        qr_scan_button.setOnClickListener {
//            val intent = Intent(this, QRCameraActivity::class.java)
//            intent.putExtra(getString(R.string.marker_location_key), currLocation)
//            startActivity(intent)
            if (!scan) {
                scan = true
                qr_scan_button.text = "STOP SCAN"
                imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), BarcodeAnalyzer {
                    if (scannedCode != it) {
                        scannedCode = it
                        Log.d("CODIGO", it)
                        Log.d("LISTA CONTIENE", listaLlaves.contains(it).toString())
                        if (listaLlaves.contains(it)) {
                            var numero = it.substring(it.length - 1).toInt()
                            if (listaDescubiertos[numero]) {
                                Toast.makeText(this, "Ya has descubierto este secreto", Toast.LENGTH_SHORT).show()
                            } else {
                                userSecretProvider.addSecretToDiscovered(uId, currLocation, numero)
                            }

                        } else {
                            Toast.makeText(this, "Error con el codigo", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                scan = false
                qr_scan_button.text = "START SCAN"
                imageAnalyzer.clearAnalyzer()
            }
        }
        outputDir = File(applicationContext.getExternalFilesDir(null).toString() + "/TouristeAR/" + currLocation)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun scanCode() {
        val imageAnalyzer = imageAnalyzer ?: return

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
                val saverUri = Uri.fromFile(photoFile)
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
                .child(currLocation).child(file.lastPathSegment.toString())

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
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onSiteDiscoveredStatus(obtained: List<Boolean>) {
        listaDescubiertos.addAll(obtained)
    }

    override fun onSecretDiscovered() {
        Toast.makeText(this, "Encontraste un Secreto", Toast.LENGTH_SHORT).show()
    }
}