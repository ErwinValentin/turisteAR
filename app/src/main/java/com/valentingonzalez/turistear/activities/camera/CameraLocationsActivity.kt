package com.valentingonzalez.turistear.activities.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.hardware.SensorManager.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.opengl.Matrix
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface.*
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.models.ARCamera
import com.valentingonzalez.turistear.models.AROverlay
import com.valentingonzalez.turistear.models.Secreto
import kotlinx.android.synthetic.main.camera_ar_location_viewer.*


class CameraLocationsActivity : AppCompatActivity(), SensorEventListener, LocationListener{

    private lateinit var surfaceView : SurfaceView
    private lateinit var cameraContainerLayout : FrameLayout
    private var arOverlayView : AROverlay? = null
    private lateinit var arCamera : ARCamera
    private var camera : Camera? = null

    private lateinit var sensorManager: SensorManager
    private val  MIN_DISTANCE_CHANGE_FOR_UPDATES = 0L // 10 meters
    private val MIN_TIME_BW_UPDATES = 0L

    private var locations = mutableListOf<Secreto>()
    private var discovered = mutableListOf<Boolean>()
    private var locationManager : LocationManager? = null
    private var location : Location? = null
    var isGPSEnabled : Boolean = true
    var isNetworkEnabled : Boolean = true
    var locationServiceAvailable : Boolean = true
    var declination = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(intent.extras != null){
            locations.add(intent.extras!!.get("SECRET1") as Secreto)
            locations.add(intent.extras!!.get("SECRET2") as Secreto)
            locations.add(intent.extras!!.get("SECRET3") as Secreto)
            discovered.add(intent.extras!!.getBoolean("DISCOVERED1"))
            discovered.add(intent.extras!!.getBoolean("DISCOVERED2"))
            discovered.add(intent.extras!!.getBoolean("DISCOVERED3"))
        }
        setContentView(R.layout.camera_ar_location_viewer)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        cameraContainerLayout = findViewById(R.id.camera_container_layout)
        surfaceView = findViewById(R.id.surface_view)
        arOverlayView = AROverlay(this , locations, discovered)
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            initARCameraView()
            initLocationService()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        registerSensors()
        initAROverlay()
    }
    override fun onPause() {
        releaseCamera();
        super.onPause();
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initARCameraView()
                initLocationService()
            }
        } else {
            Toast.makeText(this,
                    "No se tiene permiso para usar la camara",
                    Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    fun initAROverlay(){
        if (arOverlayView!!.getParent() != null) {
            (arOverlayView!!.getParent() as ViewGroup).removeView(arOverlayView)
        }
        cameraContainerLayout.addView(arOverlayView)
    }
    fun initARCameraView() {
        reloadSurfaceView()

        arCamera = ARCamera(this, surfaceView)

        if (arCamera.parent != null) {
            (arCamera.parent as ViewGroup).removeView(arCamera)
        }
        cameraContainerLayout.addView(arCamera)
        arCamera.keepScreenOn = true
        initCamera()
    }
    private fun initCamera() {
        val numCams: Int = Camera.getNumberOfCameras()
        if (numCams > 0) {
            try {
                camera = Camera.open()
                camera!!.startPreview()
                arCamera.setCamera(camera!!)
            } catch (ex: RuntimeException) {
                Toast.makeText(this, "Camera not found", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun reloadSurfaceView() {
        if (surfaceView.parent != null) {
            (surfaceView.parent as ViewGroup).removeView(surfaceView)
        }
        cameraContainerLayout.addView(surfaceView)
    }
    private fun releaseCamera() {
        if (camera != null) {
            camera!!.setPreviewCallback(null)
            camera!!.stopPreview()
            arCamera.setCamera(null)
            camera!!.release()
            camera = null
        }
    }
    private fun registerSensors() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SENSOR_DELAY_NORMAL)
    }
    companion object {
        private const val TAG = "CameraLocAR"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 11
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (accuracy == SENSOR_STATUS_UNRELIABLE) {
            Log.w("DeviceOrientation", "Orientation compass unreliable")
        }
    }
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrixFromVector = FloatArray(16)
            val rotationMatrix = FloatArray(16)
            getRotationMatrixFromVector(rotationMatrixFromVector, sensorEvent.values)
            val screenRotation = this.windowManager.defaultDisplay
                    .rotation
            when (screenRotation) {
                ROTATION_90 -> remapCoordinateSystem(rotationMatrixFromVector,
                        AXIS_Y,
                        AXIS_MINUS_X, rotationMatrix)
                ROTATION_270 -> remapCoordinateSystem(rotationMatrixFromVector,
                        AXIS_MINUS_Y,
                        AXIS_X, rotationMatrix)
                ROTATION_180 -> remapCoordinateSystem(rotationMatrixFromVector,
                        AXIS_MINUS_X, AXIS_MINUS_Y,
                        rotationMatrix)
                else -> remapCoordinateSystem(rotationMatrixFromVector,
                        AXIS_X, AXIS_Y,
                        rotationMatrix)
            }
            val projectionMatrix = arCamera.getProjectionMatrix()
            val rotatedProjectionMatrix = FloatArray(16)
            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrix, 0)
            arOverlayView!!.updateRotatedProjectionMatrix(rotatedProjectionMatrix)

            //Heading
            val orientation = FloatArray(3)
            getOrientation(rotatedProjectionMatrix, orientation)
            val bearing = Math.toDegrees(orientation[0].toDouble()) + declination
            tv_bearing.setText(String.format("Bearing: %s", bearing))
        }
    }
    private fun initLocationService() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        try {
            locationManager = (this.getSystemService(Context.LOCATION_SERVICE) as LocationManager)

            // Get GPS and network status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                locationServiceAvailable = false
            }
            locationServiceAvailable = true
            if (isNetworkEnabled) {
                locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                if (locationManager != null) {
                    location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                    updateLatestLocation(location)
                }
            }
            if (isGPSEnabled) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                if (locationManager != null) {
                    location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
                    updateLatestLocation(location)
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message)
        }
    }
    private fun updateLatestLocation(p0: Location?) {
        if (arOverlayView != null && p0 != null) {
            arOverlayView!!.updateCurrentLocation(p0)
            tv_current_location.setText(String.format("lat: %s \nlon: %s \naltitude: %s \n",
                    p0.latitude, p0.longitude, p0.altitude))
        }
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