package com.valentingonzalez.turistear.models

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.location.Location
import android.opengl.Matrix
import android.view.View
import com.valentingonzalez.turistear.utils.LocationHelper


class AROverlay(context: Context, var locations: List<Secreto>, var discovered: List<Boolean>) : View(context) {

    var rotatedprojectionMatrix = FloatArray(16)
    var cameraCoordinateVector = FloatArray(4)
    private var currLocation : Location? = null
    private var secretLocationList = mutableListOf<Location>()

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
                if(discovered[i]){
                    canvas.drawText(locations[i].nombre!!, x - (30 * locations[i].nombre!!.length / 2), y - 100, paint)
                }else{
                    canvas.drawText("???", x - (30 * "???".length / 2), y - 100, paint)
                }
                val distanceTo = "${locations[i].getLocation().distanceTo(currLocation)}m"
                canvas.drawText( distanceTo, x - (30 * distanceTo.length / 2), y - 60, paint)
            }
        }
    }
}