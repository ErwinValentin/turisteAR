package com.valentingonzalez.turistear.models

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.opengl.Matrix
import android.os.Build
import android.util.Log
import android.view.*
import java.io.IOException

@Suppress("DEPRECATION")
@TargetApi(Build.VERSION_CODES.KITKAT)
class ARCamera(context: Context, var surfaceView: SurfaceView) : ViewGroup(context), SurfaceHolder.Callback{
    var TAG = "ARCamera";
    private lateinit var surfaceHolder : SurfaceHolder
    private lateinit var previewSize: Camera.Size
    private var supportedPreviewSizes : List<Camera.Size>? = null

    private var camera: Camera? = null
    private lateinit var params : Camera.Parameters
    private var act: Activity = context as Activity

    private var projectionMatrix = FloatArray(16)
    private var cameraWidth : Int = 0
    private var cameraHeigth : Int = 0
    private val Z_NEAR = 0.5f
    private val Z_FAR = 10000f

    init{
        this.surfaceHolder = surfaceView.holder
        this.surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    fun setCamera(camera: Camera?){
        this.camera = camera
        if(this.camera != null){
            supportedPreviewSizes = this.camera!!.parameters.supportedPreviewSizes
            requestLayout()
            var params = this.camera!!.parameters
            var focusModes = params.supportedFocusModes
            if(focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                this.camera!!.parameters = params
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)

        if(supportedPreviewSizes != null){
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width,height)!!
        }
    }
    override fun onLayout(changed: Boolean, left: Int, right: Int, top: Int, bottom: Int) {
        if(changed && childCount > 0){
            var child: View = getChildAt(0)
            val width = right-left
            val height = bottom-top

            var previewWidth = width
            var previewHeight = height

            if(previewSize != null){
                previewWidth = previewSize.width
                previewHeight = previewSize.height
            }

            if (width * previewHeight > height * previewWidth) {
                var scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                var scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }
    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, width: Int, height: Int): Camera.Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = width.toDouble() / height
        if (sizes == null) return null
        var optimalSize: Camera.Size? = null
        var minDiff: Double = Double.MAX_VALUE
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue
            }
            if (Math.abs(size.height - height) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - height).toDouble()
            }
        }
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - height) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - height).toDouble()
                }
            }
        }
        if (optimalSize == null) {
            optimalSize = sizes[0]
        }
        return optimalSize
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, heigth: Int) {
        if (camera != null) {
            cameraWidth = width
            cameraHeigth = height
            val params = camera!!.parameters
            params.setPreviewSize(previewSize.width, previewSize.height)
            requestLayout()
            camera!!.parameters = params
            camera!!.startPreview()
            generateProjectionMatrix()
        }
    }
    private fun generateProjectionMatrix() {
        var ratio = 0f
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

    fun getProjectionMatrix(): FloatArray? {
        return projectionMatrix
    }
    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        if (camera != null) {
            camera!!.setPreviewCallback(null);
            camera!!.stopPreview();
            camera!!.release();
            camera = null;
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            if (camera != null) {
                params = camera!!.parameters
                val orientation: Int = getCameraOrientation()
                camera!!.setDisplayOrientation(orientation)
                camera!!.parameters.setRotation(orientation)
                camera!!.setPreviewDisplay(holder)
            }
        } catch (exception: IOException) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception)
        }
    }
    private fun getCameraOrientation(): Int {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info)
        val rotation: Int = act.getWindowManager().getDefaultDisplay().getRotation()
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var orientation: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation = (info.orientation + degrees) % 360
            orientation = (360 - orientation) % 360
        } else {
            orientation = (info.orientation - degrees + 360) % 360
        }
        return orientation
    }
}