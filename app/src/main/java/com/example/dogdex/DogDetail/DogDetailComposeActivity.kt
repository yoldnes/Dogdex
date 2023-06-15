package com.example.dogdex.DogDetail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import coil.annotation.ExperimentalCoilApi
import com.example.dogdex.DogDetail.ui.theme.DogDetailScreen
import com.example.dogdex.DogDetail.ui.theme.DogdexTheme
import com.example.dogdex.R
import com.example.dogdex.model.Dog

@ExperimentalCoilApi
class DogDetailComposeActivity : ComponentActivity() {

    companion object {
        val DOG_KEY = "dog"
        const val IS_RECOGNITION_KEY = "is_recognition"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)
        val recognition = intent?.extras?.getBoolean(IS_RECOGNITION_KEY, false) ?: false

        if (dog == null) {
            Toast.makeText(this, R.string.error_show_not_found, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContent {
            DogdexTheme {
                DogDetailScreen(dog = dog)
            }
        }
    }
}