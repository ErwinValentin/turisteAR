package com.valentingonzalez.turistear

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(private val result: (String) -> Unit) : ImageAnalysis.Analyzer{
    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val barcodeScanner = BarcodeScanning.getClient()

        if(mediaImage!=null){
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(image)
                    .addOnSuccessListener {
                        Log.d("BARCODE", it.size.toString())
                        if(it.size>0){
                            result.invoke(it[0].rawValue.toString())
                        }
                        mediaImage.close()
                        imageProxy.close()
                    }.addOnFailureListener{e ->
                        Log.d("BARCODE ERROR", "Error", e)
                        mediaImage.close()
                        imageProxy.close()
                    }
        }else{
            imageProxy.close()
        }
    }

}