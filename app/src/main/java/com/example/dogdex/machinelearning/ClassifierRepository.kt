package com.example.dogdex.machinelearning

import android.annotation.SuppressLint
import android.graphics.*
import androidx.camera.core.ImageProxy
import com.hackaprende.dogedex.machinelearning.Classifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ClassifierRepository(private val classifier: Classifier) {

    suspend fun recognizeImage(imageProxy: ImageProxy): DogRecognition =
        withContext(Dispatchers.IO) {
            val bitmap = convertImageProxyToBitmap(imageProxy)
            if (bitmap == null) {
                DogRecognition("", 0f)
            } else {
                classifier.recognizeImage(bitmap).first()
            }
        }

    @SuppressLint("UnsafeOptInUsageError")
    private fun convertImageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped\
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}