package com.valentingonzalez.turistear.activities.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.opengl.Matrix
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import com.squareup.picasso.Picasso
import com.valentingonzalez.turistear.utils.BarcodeAnalyzer
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.AROverlay
import com.valentingonzalez.turistear.models.Secreto
import com.valentingonzalez.turistear.providers.SecretProvider
import com.valentingonzalez.turistear.providers.UserSecretProvider
import kotlinx.android.synthetic.main.camera_ar_layout.*
import kotlinx.android.synthetic.main.camera_ar_location_viewer.*
import kotlinx.android.synthetic.main.search_options_layout.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.HashMap

class ARCameraActivity : AppCompatActivity(), UserSecretProvider.UserSecrets, SecretProvider.SiteSecrets, SensorEventListener, LocationListener{
    private var imageCapture: ImageCapture? = null
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var outputDir: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var markerLocation: String
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest


    private lateinit var sensorManager: SensorManager
    private var locationManager : LocationManager?= null
    private val  MIN_DISTANCE_CHANGE_FOR_UPDATES = 0L // 10 meters
    private val MIN_TIME_BW_UPDATES = 0L
    private var projectionMatrix = FloatArray(16)
    private val Z_NEAR = 0.5f
    private val Z_FAR = 10000f

    private var arOverlayView : AROverlay? = null

    val userSecretProvider = UserSecretProvider(this)
    private val secretProvider = SecretProvider(this)
//    private val latlngList = mutableListOf<LocationData>()
    private var listaLlaves = mutableListOf<String>()
    private var listaDescubierto : List<Boolean> = listOf(false,false, false)
    private lateinit var listaDescubiertos: HashMap<Int, Boolean>
    private var listaSecretos = mutableListOf<Secreto>()
    val uId = FirebaseAuth.getInstance().uid.toString()

    private var trackLocations : Boolean = false
    var isGPSEnabled : Boolean = true
    var isNetworkEnabled : Boolean = true
    var locationServiceAvailable : Boolean = true
    var declination = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_ar_layout)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        markerLocation = intent.getStringExtra(getString(R.string.marker_location_key))!!
        arOverlayView = AROverlay(this,listaSecretos, listaDescubierto,markerLocation)

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 1000

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                p0 ?: return
                for(location in p0.locations){
                    updateLatestLocation(location)
                }
            }
        }
        val lastLocation = fusedLocationProviderClient.lastLocation
        lastLocation.addOnSuccessListener { location ->
            if(location != null){
                currentLocation =location
            }
        }


        for (i in 0..2) {
            listaLlaves.add(markerLocation + i)
        }

        secretProvider.getSecrets(markerLocation)
        userSecretProvider.getSiteDiscoveredSecrets(uId, markerLocation)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        camera_capture_button.setOnClickListener { takePhoto() }
        var scan = false
        var scannedCode = ""
        qr_scan_button.setOnClickListener { _ ->
            if (!scan) {
                scan = true
                qr_scan_button.setImageResource(R.drawable.qr_white_cancel)
                imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor(), BarcodeAnalyzer {
                    if (scannedCode != it) {
                        scannedCode = it
                        if (listaLlaves.contains(it)) {
                            var numero = it.substring(it.length - 1).toInt()
                            if (listaDescubiertos[numero] != false) {
                                Toast.makeText(this, "Ya has descubierto este secreto", Toast.LENGTH_SHORT).show()
                            } else {
                                val secreto = listaSecretos[numero]
                                val loc = Location("")
                                loc.latitude = secreto.latitud!!
                                loc.longitude = secreto.longitud!!
                                if (loc.distanceTo(currentLocation) < 20) {
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

        show_locations_button.setOnClickListener{
            listaDescubierto  = listaDescubiertos.values.toList()
            trackLocations = !trackLocations
            if(trackLocations){
                show_locations_button.setImageResource(R.drawable.cancel_location_sm)
                startLocationUpdates()
                registerSensors()
                initAROverlay()
            }else{
                show_locations_button.setImageResource(R.drawable.ic_location_on_white_24dp)
                stopLocationUpdates()
                removeSensor()
                removeAROverlay()
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
                        val surfaceProvider = viewFinder.createSurfaceProvider()
                        it.setSurfaceProvider(surfaceProvider)

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

//    private fun initLocationService() {
//        if (Build.VERSION.SDK_INT >= 23 &&
//                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//        try {
//            locationManager = (this.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
//
//            // Get GPS and network status
//            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
//            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//            if (!isNetworkEnabled && !isGPSEnabled) {
//                // cannot get location
//                locationServiceAvailable = false
//            }
//            locationServiceAvailable = true
//            if (isNetworkEnabled) {
//                locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                        MIN_TIME_BW_UPDATES,
//                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
//                if (locationManager != null) {
//                    currentLocation = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
//                    updateLatestLocation(currentLocation)
//                }
//            }
//            if (isGPSEnabled) {
//                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                        MIN_TIME_BW_UPDATES,
//                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
//                if (locationManager != null) {
//                    currentLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
//                    updateLatestLocation(currentLocation)
//                }
//            }
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//        }
//    }
//    private fun stopLocationService(){
//        locationManager!!.removeUpdates(this)
//    }
    private fun startLocationUpdates(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }
    private fun stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
    private fun updateLatestLocation(p0: Location?) {
        if (arOverlayView != null && p0 != null) {
            arOverlayView!!.updateCurrentLocation(p0)
//            tv_current_location.setText(String.format("lat: %s \nlon: %s \naltitude: %s \n",
//                    p0.latitude, p0.longitude, p0.altitude))
        }
    }
    fun registerSensors(){
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL)
    }
    fun removeSensor(){
        sensorManager.unregisterListener(this)
    }
    fun initAROverlay(){
        if (arOverlayView!!.parent != null) {
            (arOverlayView!!.parent as ViewGroup).removeView(arOverlayView)
        }
        frame_container.addView(arOverlayView)
    }
    fun removeAROverlay(){
        if (arOverlayView!!.parent != null) {
            (arOverlayView!!.parent as ViewGroup).removeView(arOverlayView)
        }
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
        if(listaDescubiertos.isNotEmpty()) {
            listaDescubierto = listaDescubiertos.values.toList()
        }
//        Log.d("TESTDESC", listaDescubierto.size.toString()+ " "+listaDescubierto.toString())
        if(arOverlayView!= null) {
            arOverlayView!!.updateDiscovered(listaDescubierto)
        }
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.w("DeviceOrientation", "Orientation compass unreliable")
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrixFromVector = FloatArray(16)
            val rotationMatrix = FloatArray(16)
            SensorManager.getRotationMatrixFromVector(rotationMatrixFromVector, sensorEvent.values)
            val screenRotation = this.windowManager.defaultDisplay
                    .rotation
            when (screenRotation) {
                Surface.ROTATION_90 -> SensorManager.remapCoordinateSystem(rotationMatrixFromVector,
                        SensorManager.AXIS_Y,
                        SensorManager.AXIS_MINUS_X, rotationMatrix)
                Surface.ROTATION_270 -> SensorManager.remapCoordinateSystem(rotationMatrixFromVector,
                        SensorManager.AXIS_MINUS_Y,
                        SensorManager.AXIS_X, rotationMatrix)
                Surface.ROTATION_180 -> SensorManager.remapCoordinateSystem(rotationMatrixFromVector,
                        SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                        rotationMatrix)
                else -> SensorManager.remapCoordinateSystem(rotationMatrixFromVector,
                        SensorManager.AXIS_X, SensorManager.AXIS_Y,
                        rotationMatrix)
            }
            generateProjectionMatrix()
            val rotatedProjectionMatrix = FloatArray(16)
            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrix, 0)
            arOverlayView!!.updateRotatedProjectionMatrix(rotatedProjectionMatrix)

            //Heading
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotatedProjectionMatrix, orientation)
            //val bearing = Math.toDegrees(orientation[0].toDouble()) + declination
            //tv_bearing.setText(String.format("Bearing: %s", bearing))
        }
    }
    private fun generateProjectionMatrix() {
        var cameraWidth = viewFinder.measuredWidth
        var cameraHeigth = viewFinder.measuredHeight
        var ratio : Float
        if (cameraWidth < cameraHeigth) {
            ratio = cameraWidth.toFloat() / cameraHeigth
        } else {
            ratio = cameraHeigth.toFloat() / cameraWidth
        }
        val OFFSET = 0
        val LEFT = -ratio
        val RIGHT = ratio
        val BOTTOM = -1f
        val TOP = 1f
        Matrix.frustumM(projectionMatrix, OFFSET, LEFT, RIGHT, BOTTOM, TOP, Z_NEAR, Z_FAR)
    }
    override fun onLocationChanged(p0: Location?) {
        updateLatestLocation(p0)
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }
}