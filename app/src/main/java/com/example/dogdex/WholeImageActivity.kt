package com.example.dogdex

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import coil.load
import com.example.dogdex.databinding.ActivityWholeImageBinding
import java.io.File

const val PHOTO_KEY = "photo_uri"

class WholeImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWholeImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWholeImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUrl = intent.extras?.getString(PHOTO_KEY)
        val uri = Uri.parse(photoUrl)

        val path = uri.path
        if (path == null) {
            Toast.makeText(this, "Error show image no photo", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        binding.dogPhoto.load(File(path))
    }
}