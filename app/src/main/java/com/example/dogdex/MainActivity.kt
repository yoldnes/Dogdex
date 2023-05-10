package com.example.dogdex

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.dogdex.Settings.SettingActivity
import com.example.dogdex.api.ApiServiceInterceptor
import com.example.dogdex.auth.LoginActivity
import com.example.dogdex.databinding.ActivityMainBinding
import com.example.dogdex.doglist.DogListActivity
import com.example.dogdex.model.User
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var isCameraReady: Boolean = false
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService


    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                setUpCamera()
            } else {
                Toast.makeText(
                    this,
                    "You need to accept permission camera to used ccamera",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateSession()
        initView()
        requestPermitionCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }

    private fun initView() {
        with(binding) {
            settingsFab.setOnClickListener {
                openSettingActivity()
            }
            dogListFab.setOnClickListener {
                openDogListActivity()
            }
            takePhotoFab.setOnClickListener {
                if (isCameraReady) {
                    takePhoto()
                }
            }
        }
    }

    private fun validateSession() {
        val user = User.getLoggedInUser(this)

        if (user == null) {
            openLogin()
            return
        } else {
            ApiServiceInterceptor.setSessionToken(user.authenticationToken)
        }
    }

    private fun requestPermitionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    setUpCamera()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    AlertDialog.Builder(this)
                        .setTitle("Aceptame por favor")
                        .setMessage("Aceptame o me da ansiedad")
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            requestPermissionLauncher.launch(
                                Manifest.permission.CAMERA
                            )
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ ->
                        }.show()

                }
                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            }
        } else {
            setUpCamera()
        }
    }


    private fun setUpCamera() {
        binding.containerPreView.post {
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(binding.containerPreView.display.rotation)
                .build()
            cameraExecutor = Executors.newSingleThreadExecutor()
            starCamera()
            isCameraReady = true
        }
    }

    private fun takePhoto() {
        val outputFileOptions =
            ImageCapture.OutputFileOptions.Builder(getOutputPhotoField()).build()
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error take photo ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val url = outputFileResults.savedUri
                    openWholeImage(url.toString())
                }
            })
    }

    private fun openWholeImage(photoUri: String) {
        val intent = Intent(this, WholeImageActivity::class.java)
        intent.putExtra(PHOTO_KEY, photoUri)
        startActivity(intent)
    }

    private fun getOutputPhotoField(): File {
        val mediaDir = externalMediaDirs.firstOrNull().let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            filesDir
        }
    }

    private fun starCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.containerPreView.surfaceProvider)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                imageProxy.close()
            }

            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview, imageCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun openDogListActivity() {
        startActivity(Intent(this@MainActivity, DogListActivity::class.java))
    }

    private fun openSettingActivity() {
        startActivity(Intent(this@MainActivity, SettingActivity::class.java))
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}