package com.example.dogdex.Main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.Manifest
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import coil.annotation.ExperimentalCoilApi
import com.example.dogdex.DogDetail.DogDetailActivity
import com.example.dogdex.DogDetail.DogDetailComposeActivity
import com.example.dogdex.LABEL_PATH
import com.example.dogdex.MODEL_PATTH
import com.example.dogdex.PHOTO_KEY
import com.example.dogdex.R
import com.example.dogdex.Settings.SettingActivity
import com.example.dogdex.WholeImageActivity
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.api.ApiServiceInterceptor
import com.example.dogdex.auth.LoginActivity
import com.example.dogdex.databinding.ActivityMainBinding
import com.example.dogdex.doglist.DogListActivity
import com.example.dogdex.machinelearning.DogRecognition
import com.example.dogdex.model.Dog
import com.example.dogdex.model.User
import com.hackaprende.dogedex.machinelearning.Classifier
import org.tensorflow.lite.support.common.FileUtil
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalCoilApi
class MainActivity : AppCompatActivity() {

    private var isCameraReady: Boolean = false
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var classifier: Classifier
    private val viewModel: MainViewModel by viewModels()

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
        onObserver()
    }

    private fun onObserver() {
        viewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseState.Loading -> binding.progress.visibility = View.VISIBLE
                is ApiResponseState.Success -> binding.progress.visibility = View.GONE
                is ApiResponseState.Error -> {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(this, status.messageId, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.dog.observe(this) { dog ->
            if (dog != null) {
                openDodDetail(dog)
            }
        }

        viewModel.dogRecognition.observe(this) {
            enableTakePhotoButtom(it)
        }
    }

    private fun openDodDetail(dog: Dog) {
        val intent = Intent(this, DogDetailComposeActivity::class.java)
        intent.putExtra(DogDetailComposeActivity.DOG_KEY, dog)
        intent.putExtra(DogDetailComposeActivity.IS_RECOGNITION_KEY, true)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.setUpClassifier(
            FileUtil.loadMappedFile(this@MainActivity, MODEL_PATTH),
            FileUtil.loadLabels(this@MainActivity, LABEL_PATH)
        )
    }

    private fun initView() {
        with(binding) {
            settingsFab.setOnClickListener {
                openSettingActivity()
            }
            dogListFab.setOnClickListener {
                openDogListActivity()
            }
            /*takePhotoFab.setOnClickListener {
                if (isCameraReady) {
                    takePhoto()
                }
            }*/
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
                    /*val url = outputFileResults.savedUri
                    val bitmap = BitmapFactory.decodeFile(url?.path)
                    val dogRecognition = classifier.recognizeImage(bitmap).first()
                    viewModel.getDogByMLId(dogRecognition.id)
                    openWholeImage(url.toString())*/
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
                viewModel.recognizeImage(imageProxy)
            }

            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview, imageCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun enableTakePhotoButtom(dogRecognition: DogRecognition) {
        with(binding) {
            if (dogRecognition.confidence > 70.0) {
                takePhotoFab.alpha = 1f
                takePhotoFab.setOnClickListener {
                    viewModel.getDogByMLId(dogRecognition.id)
                }
            } else {
                takePhotoFab.alpha = 0.2f
                takePhotoFab.setOnClickListener { null }
            }
        }
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