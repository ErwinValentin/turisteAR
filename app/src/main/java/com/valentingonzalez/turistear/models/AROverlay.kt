package com.valentingonzalez.turistear.models

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.location.Location
import android.opengl.Matrix
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.activities.camera.ARCameraActivity
import com.valentingonzalez.turistear.activities.maps.SecretDetailActivity
import com.valentingonzalez.turistear.providers.UserSecretProvider
import com.valentingonzalez.turistear.utils.LocationHelper


class AROverlay(context: Context, var locations: List<Secreto>, var discovered: List<Boolean>, var location: String) : View(context) {

    var rotatedprojectionMatrix = FloatArray(16)
    var cameraCoordinateVector = FloatArray(4)
    private var currLocation : Location? = null
    //private var secretLocationList = mutableListOf<Location>()
    private var rectangles = listOf(Rect(),Rect(),Rect())
    private var distances = mutableListOf(0f,0f,0f)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun updateRotatedProjectionMatrix(newMatrix: FloatArray){
        this.rotatedprojectionMatrix = newMatrix
        this.invalidate()
    }

    fun updateCurrentLocation(newLocation: Location){
        this.currLocation = newLocation
        this.invalidate()
        for (secreto in locations) {
            secreto.altitud = currLocation!!.altitude
        }
    }

    fun updateDiscovered(newDiscovered: List<Boolean>){
        this.discovered = newDiscovered
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        val x = event!!.x.toInt()
        val y = event.y.toInt()
        if(event.action == MotionEvent.ACTION_UP ){
            for(i in rectangles.indices){
                val rect = rectangles[i]
                if(rect.contains(x,y)){
                    if(discovered[i]){
                        startSecretDetailActivity()
                    }else{
                        if(distances[i] < 20f){
                            (context as ARCameraActivity).userSecretProvider.addSecretToDiscovered(FirebaseAuth.getInstance().uid!!, location, i, locations[i].nombre!!)
                            //startSecretDetailActivity()
                        }
                    }
                    Toast.makeText(context,locations[i].nombre,Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true
    }

    private fun startSecretDetailActivity() {
        val intent = Intent(context, SecretDetailActivity::class.java)
        intent.putExtra(context.getString(R.string.marker_location_key), location)
        context.startActivity(intent)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if(currLocation == null){
            return
        }

        val radius = 30f


        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 45f


        for(i in locations.indices){
            val currentLocationInECEF = LocationHelper.WSG84toECEF(currLocation!!)
            val pointInECEF = LocationHelper.WSG84toECEF(locations[i].getLocation())
            val pointInENU = LocationHelper.ECEFtoENU(currLocation!!, currentLocationInECEF, pointInECEF)

            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedprojectionMatrix, 0 , pointInENU, 0)

            if(cameraCoordinateVector[2]< 0){
                val x = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * width
                val y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3])* height

                canvas!!.drawCircle(x,y,radius, paint)
                rectangles[i].set((x-15f).toInt(),(y-15f).toInt(),(x+15f).toInt(),(y+15f).toInt())
                if(discovered[i]){
                    canvas.drawText(locations[i].nombre!!, x - (30 * locations[i].nombre!!.length / 2), y - 100, paint)
                }else{
                    canvas.drawText("???", x - (30 * "???".length / 2), y - 100, paint)
                }
                val dist = locations[i].getLocation().distanceTo(currLocation)
                val distanceTo = "${dist.format(2)}m"
                distances[i] = dist
                canvas.drawText( distanceTo, x - (30 * distanceTo.length / 2), y - 60, paint)
            }
        }
    }
}

private fun Float.format(i: Int): String {
    return  "%.${i}f".format(this)
}
